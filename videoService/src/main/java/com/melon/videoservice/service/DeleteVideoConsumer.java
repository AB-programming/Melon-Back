package com.melon.videoservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.melon.baseservice.service.HdfsService;
import com.melon.videoservice.mapper.CollectMapper;
import com.melon.videoservice.mapper.CommentMapper;
import com.melon.videoservice.mapper.LikeMapper;
import com.melon.videoservice.mapper.ReplyMapper;
import com.melon.videoservice.pojo.entity.Collect;
import com.melon.videoservice.pojo.entity.Comment;
import com.melon.videoservice.pojo.entity.Like;
import com.melon.videoservice.pojo.entity.Reply;
import com.melon.videoservice.pojo.message.DeleteVideoMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RocketMQMessageListener(topic = "video-delete-topic", consumerGroup = "video-consumer-group")
@Slf4j
public class DeleteVideoConsumer implements RocketMQListener<DeleteVideoMessage> {
    @Resource
    private HdfsService hdfsService;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private CollectMapper collectMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ReplyMapper replyMapper;

    @Override
    public void onMessage(DeleteVideoMessage deleteVideoMessage) {
        String videoId = deleteVideoMessage.getVideoId();
        hdfsService.delete(deleteVideoMessage.getVideoPath(), true);
        hdfsService.delete(deleteVideoMessage.getCoverPath(), true);
        // delete video like when delete video
        List<String> likeIds = likeMapper.selectList(
                new LambdaQueryWrapper<Like>()
                        .select(Like::getId)
                        .eq(Like::getVideoId, videoId)
        ).stream().map(Like::getId).toList();
        if (!likeIds.isEmpty()) {
            if (likeMapper.deleteByIds(likeIds) < likeIds.size()) {
                log.error("Delete video like failed, videoId={}", videoId);
            }
        }
        // delete video collect when delete video
        List<String> collectIds = collectMapper.selectList(
                new LambdaQueryWrapper<Collect>()
                        .select(Collect::getId)
                        .eq(Collect::getVideoId, videoId)
        ).stream().map(Collect::getId).toList();
        if (!collectIds.isEmpty()) {
            if (collectMapper.deleteByIds(collectIds) < collectIds.size()) {
                log.error("Delete video collect failed, videoId={}", videoId);
            }
        }
        // delete comments and replies when delete video
        List<String> commentIds = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .select(Comment::getId)
                        .eq(Comment::getVideoId, videoId)
        ).stream().map(Comment::getId).toList();
        if (!commentIds.isEmpty()) {
            List<String> replyIds = replyMapper.selectList(
                    new LambdaQueryWrapper<Reply>()
                            .select(Reply::getId)
                            .in(Reply::getCommentId, commentIds)
            ).stream().map(Reply::getId).toList();
            if (!replyIds.isEmpty()) {
                if (replyMapper.deleteByIds(replyIds) < replyIds.size()) {
                    log.error("Delete replies failed for videoId={}", videoId);
                }
            }
            if (commentMapper.deleteByIds(commentIds) < commentIds.size()) {
                log.error("Delete comments failed for videoId={}", videoId);
            }
        }
    }
}

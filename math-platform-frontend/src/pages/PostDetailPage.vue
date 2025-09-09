<template>
  <div class="post-detail-page">
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :md="17">
        <!-- 帖子内容 -->
        <a-card :loading="loading" class="post-card">
          <template #title>
            <div class="post-title">{{ post?.title }}</div>
          </template>
          <div v-if="post">
            <div class="post-meta">
              <a-avatar :src="post.userAvatar" size="small">{{ post.userName?.charAt(0) }}</a-avatar>
              <span class="meta-user">{{ post.userName || '用户' }}</span>
              <a-tag class="meta-category">{{ post.category }}</a-tag>
              <span class="meta-time">{{ formatTime(post.createTime) }}</span>
            </div>

            <div class="post-content" v-html="renderMd(post.content || '')"></div>

            <div v-if="post.images" class="post-images">
              <a-image-preview-group>
                <a-image
                  v-for="img in splitImages(post.images as any)"
                  :key="img"
                  :src="img"
                  :width="120"
                  class="post-image"
                />
              </a-image-preview-group>
            </div>

            <div v-if="post.tags" class="post-tags">
              <a-tag v-for="t in splitTags(post.tags)" :key="t" @click="goTag(t)">{{ t }}</a-tag>
            </div>

            <div class="post-actions">
              <a-space>
                <a-button :type="hasLiked ? 'primary' : 'default'" @click="onToggleLike" :loading="likeLoading">
                  <template #icon>
                    <LikeOutlined />
                  </template>
                  {{ hasLiked ? '已赞' : '点赞' }} {{ post.likeCount || 0 }}
                </a-button>
                <a-button :type="hasFavourited ? 'primary' : 'default'" @click="onToggleFavourite" :loading="favLoading">
                  <template #icon>
                    <StarOutlined />
                  </template>
                  {{ hasFavourited ? '已收藏' : '收藏' }} {{ post.favouriteCount || 0 }}
                </a-button>
                <a-button>
                  <template #icon>
                    <EyeOutlined />
                  </template>
                  浏览 {{ post.viewCount || 0 }}
                </a-button>
              </a-space>
            </div>
          </div>
        </a-card>

        <!-- 评论区 -->
        <a-card class="comment-card" title="评论">
          <div class="comment-editor">
            <a-textarea v-model:value="newComment" :rows="4" placeholder="友善发言，理性讨论" />
            <div class="editor-actions">
              <a-button type="primary" :loading="commentSubmitting" @click="submitComment">发表评论</a-button>
            </div>
          </div>

          <a-list
            :data-source="comments"
            :loading="commentLoading"
            :locale="{ emptyText: '暂时还没有评论' }"
          >
            <template #renderItem="{ item }">
              <a-list-item class="comment-item">
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :src="item.userAvatar">{{ item.userName?.charAt(0) }}</a-avatar>
                  </template>
                  <template #title>
                    <div class="comment-header">
                      <span class="comment-user">{{ item.userName || '用户' }}</span>
                      <span class="comment-time">{{ formatTime(item.createTime) }}</span>
                    </div>
                  </template>
                  <template #description>
                    <div class="comment-content">{{ item.content }}</div>
                    <div class="comment-actions">
                      <a-space>
                        <a-button size="small" type="text" @click="openReply(item)">回复</a-button>
                        <a-button size="small" type="text" v-if="isOwner(item)" @click="openEdit(item)">编辑</a-button>
                        <a-popconfirm
                          v-if="isOwner(item)"
                          title="确定删除该评论？"
                          ok-text="删除"
                          cancel-text="取消"
                          @confirm="deleteComment(item)"
                        >
                          <a-button size="small" type="text" danger>删除</a-button>
                        </a-popconfirm>
                        <a-button size="small" type="text" @click="toggleCommentLike(item)" :loading="item._likeLoading">
                          <template #icon>
                            <LikeOutlined />
                          </template>
                          {{ item.likeCount || 0 }}
                        </a-button>
                      </a-space>
                    </div>

                    <div v-if="item._showReply" class="reply-editor">
                      <a-textarea v-model:value="item._replyText" :rows="3" placeholder="回复 {{ item.userName }}" />
                      <div class="editor-actions">
                        <a-space>
                          <a-button size="small" @click="item._showReply = false">取消</a-button>
                          <a-button size="small" type="primary" :loading="item._replyLoading" @click="submitReply(item)">回复</a-button>
                        </a-space>
                      </div>
                    </div>

                    <div v-if="item._showEdit" class="reply-editor">
                      <a-textarea v-model:value="item._editText" :rows="3" placeholder="编辑评论" />
                      <div class="editor-actions">
                        <a-space>
                          <a-button size="small" @click="item._showEdit = false">取消</a-button>
                          <a-button size="small" type="primary" :loading="item._editLoading" @click="submitEdit(item)">保存</a-button>
                        </a-space>
                      </div>
                    </div>

                    <div v-if="item._replies && item._replies.length" class="reply-list">
                      <div class="reply-item" v-for="reply in item._replies" :key="reply.id">
                        <div class="reply-meta">
                          <a-avatar :src="reply.userAvatar" size="small">{{ reply.userName?.charAt(0) }}</a-avatar>
                          <span class="reply-user">{{ reply.userName || '用户' }}</span>
                          <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                        </div>
                        <div class="reply-content">{{ reply.content }}</div>
                      </div>
                    </div>

                    <div v-if="(item.replyCount || 0) > 0 && (!item._replies || item._replies.length === 0)" class="load-replies">
                      <a-button type="link" size="small" @click="loadReplies(item)">展开 {{ item.replyCount }} 条回复</a-button>
                    </div>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>

          <div v-if="hasMoreComments" class="load-more">
            <a-button :loading="commentLoading" @click="loadMoreComments">加载更多</a-button>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="7">
        <a-card title="相关推荐" class="side-card">
          <a-empty description="敬请期待" />
        </a-card>
      </a-col>
    </a-row>
  </div>
  
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { LikeOutlined, StarOutlined, EyeOutlined } from '@ant-design/icons-vue'
import * as postController from '@/api/postController'
import * as socialController from '@/api/socialController'
import * as commentController from '@/api/commentController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { renderMarkdownWithKatex } from '@/utils/markdown'

const route = useRoute()
const postId = Number(route.params.id)
const loginUserStore = useLoginUserStore()

const loading = ref(false)
const post = ref<API.Post | null>(null)

const likeLoading = ref(false)
const favLoading = ref(false)
const hasLiked = ref(false)
const hasFavourited = ref(false)

const newComment = ref('')
const commentLoading = ref(false)
const commentSubmitting = ref(false)
const comments = ref<any[]>([])
const current = ref(1)
const pageSize = ref(10)
const hasMoreComments = ref(true)

const splitTags = (tags?: string | string[]) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  try {
    const arr = JSON.parse(tags)
    return Array.isArray(arr) ? arr : (tags ? tags.split(',').filter(Boolean) : [])
  } catch {
    return tags.split(',').filter(Boolean)
  }
}
const splitImages = (images?: string | string[]) => {
  if (!images) return []
  if (Array.isArray(images)) return images
  try {
    const arr = JSON.parse(images)
    return Array.isArray(arr) ? arr : (images ? images.split(',').filter(Boolean) : [])
  } catch {
    return images.split(',').filter(Boolean)
  }
}
const renderMd = (md: string) => renderMarkdownWithKatex(md)
const formatTime = (val: any) => {
  try {
    const d = new Date(val)
    return isNaN(d.getTime()) ? '' : d.toLocaleString()
  } catch { return '' }
}

const loadPost = async () => {
  loading.value = true
  try {
    const res = await postController.getPostByIdUsingGet({ id: postId })
    if (res?.data?.code === 0) {
      post.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}

const loadUserInteractState = async () => {
  try {
    const [liked, fav] = await Promise.all([
      socialController.isPostLikedByUserUsingGet({ postId }),
      socialController.isPostFavouritedByUserUsingGet({ postId })
    ])
    if (liked?.data?.code === 0) hasLiked.value = !!liked.data.data
    if (fav?.data?.code === 0) hasFavourited.value = !!fav.data.data
  } catch {}
}

const onToggleLike = async () => {
  if (!post.value) return
  likeLoading.value = true
  const old = hasLiked.value
  hasLiked.value = !old
  post.value.likeCount = (post.value.likeCount || 0) + (old ? -1 : 1)
  try {
    const res = await postController.togglePostLikeUsingPost({ postId })
    if (res?.data?.code !== 0) throw new Error('failed')
  } catch {
    // rollback
    hasLiked.value = old
    if (post.value) post.value.likeCount = (post.value.likeCount || 0) + (old ? 1 : -1)
    message.error('操作失败，请稍后重试')
  } finally {
    likeLoading.value = false
  }
}

const onToggleFavourite = async () => {
  if (!post.value) return
  favLoading.value = true
  const old = hasFavourited.value
  hasFavourited.value = !old
  post.value.favouriteCount = (post.value.favouriteCount || 0) + (old ? -1 : 1)
  try {
    const res = await postController.togglePostFavouriteUsingPost({ postId })
    if (res?.data?.code !== 0) throw new Error('failed')
  } catch {
    hasFavourited.value = old
    if (post.value) post.value.favouriteCount = (post.value.favouriteCount || 0) + (old ? 1 : -1)
    message.error('操作失败，请稍后重试')
  } finally {
    favLoading.value = false
  }
}

const loadComments = async (reset = false) => {
  if (commentLoading.value) return
  commentLoading.value = true
  try {
    if (reset) {
      current.value = 1
      comments.value = []
      hasMoreComments.value = true
    }
    const res = await commentController.listCommentsByPostIdUsingPost({
      postId,
      current: current.value,
      pageSize: pageSize.value,
    })
    if (res?.data?.code === 0 && res.data.data) {
      const list = (res.data.data.records || []).map((c: any) => ({...c}))
      comments.value.push(...list)
      hasMoreComments.value = current.value < (res.data.data.pages || 1)
      if (hasMoreComments.value) current.value++
    }
  } finally {
    commentLoading.value = false
  }
}

const loadMoreComments = () => loadComments(false)

const submitComment = async () => {
  const content = newComment.value.trim()
  if (!content) return message.warning('请输入评论内容')
  commentSubmitting.value = true
  try {
    const res = await commentController.addCommentUsingPost({ content, postId })
    if (res?.data?.code === 0) {
      message.success('评论成功')
      newComment.value = ''
      await loadComments(true)
    } else {
      message.error(res?.data?.message || '评论失败')
    }
  } finally {
    commentSubmitting.value = false
  }
}

const openReply = (c: any) => {
  c._showReply = true
  c._replyText = ''
}

const isOwner = (c: any) => {
  return !!loginUserStore.loginUser?.id && loginUserStore.loginUser.id === c.userId
}

const openEdit = (c: any) => {
  c._showEdit = true
  c._editText = c.content
}

const submitEdit = async (c: any) => {
  const content = (c._editText || '').trim()
  if (!content) return message.warning('请输入内容')
  c._editLoading = true
  try {
    const res = await commentController.updateCommentUsingPost({ id: c.id, content })
    if (res?.data?.code === 0) {
      c.content = content
      c._showEdit = false
      message.success('已更新')
    } else {
      message.error(res?.data?.message || '更新失败')
    }
  } finally {
    c._editLoading = false
  }
}

const deleteComment = async (c: any) => {
  try {
    const res = await commentController.deleteCommentUsingPost({ id: c.id })
    if (res?.data?.code === 0) {
      comments.value = comments.value.filter((x) => x.id !== c.id)
      message.success('已删除')
    } else {
      message.error(res?.data?.message || '删除失败')
    }
  } catch {
    message.error('删除失败')
  }
}

const submitReply = async (c: any) => {
  const content = (c._replyText || '').trim()
  if (!content) return message.warning('请输入回复内容')
  c._replyLoading = true
  try {
    const res = await commentController.addReplyCommentUsingPost({ content, postId, parentId: c.id })
    if (res?.data?.code === 0) {
      message.success('回复成功')
      c._showReply = false
      c._replyText = ''
      await loadReplies(c, true)
    } else {
      message.error(res?.data?.message || '回复失败')
    }
  } finally {
    c._replyLoading = false
  }
}

const toggleCommentLike = async (c: any) => {
  c._likeLoading = true
  const oldCount = c.likeCount || 0
  c.likeCount = oldCount + 1 // 简单加1作为演示，后端未提供是否已点赞查询
  try {
    const res = await commentController.toggleCommentLikeUsingPost({ commentId: c.id })
    if (res?.data?.code !== 0) throw new Error('failed')
  } catch {
    c.likeCount = oldCount
    message.error('操作失败，请稍后重试')
  } finally {
    c._likeLoading = false
  }
}

const loadReplies = async (c: any, force = false) => {
  if (c._replies && c._replies.length && !force) return
  try {
    const res = await commentController.listRepliesByParentIdUsingGet({ parentId: c.id })
    if (res?.data?.code === 0) {
      c._replies = res.data.data || []
    }
  } catch {}
}

const goTag = (t: string) => {
  window.location.href = `/search?q=${encodeURIComponent(t)}`
}

onMounted(async () => {
  await loadPost()
  await loadUserInteractState()
  await loadComments(true)
})
</script>

<style scoped lang="scss">
.post-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  .post-card {
    border-radius: 12px;

    .post-title {
      font-size: 20px;
      font-weight: 600;
    }

    .post-meta {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #8c8c8c;
      margin-bottom: 12px;

      .meta-user { margin-left: 4px; }
      .meta-category { margin-left: 8px; }
      .meta-time { margin-left: auto; font-size: 12px; }
    }

    .post-content {
      padding: 12px 0 4px;
      line-height: 1.8;
      white-space: normal;
      word-break: break-word;
    }

    .post-tags { margin-top: 12px; }

    .post-actions { margin-top: 16px; }
  }

  .comment-card {
    border-radius: 12px;
    margin-top: 16px;

    .comment-editor {
      margin-bottom: 16px;
      .editor-actions { margin-top: 8px; text-align: right; }
    }

    .comment-item {
      .comment-header { display: flex; gap: 8px; align-items: center; }
      .comment-user { font-weight: 500; }
      .comment-time { color: #8c8c8c; font-size: 12px; }
      .comment-content { margin-top: 6px; }
      .comment-actions { margin-top: 6px; }
      .reply-editor { margin-top: 8px; }
      .reply-list { margin-top: 8px; padding-left: 36px; }
      .reply-item { padding: 6px 0; }
      .reply-meta { display: flex; gap: 6px; align-items: center; color: #8c8c8c; font-size: 12px; }
      .reply-content { margin-left: 28px; }
      .load-replies { margin-top: 6px; }
    }
  }

  .side-card { border-radius: 12px; }
}
</style>



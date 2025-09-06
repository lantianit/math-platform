<template>
  <div class="post-create-page">
    <a-card title="发布新帖子" class="create-card">
      <a-form :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }" @submit.prevent>
        <a-form-item label="标题" required>
          <a-input v-model:value="form.title" placeholder="请输入标题" :maxlength="80" />
        </a-form-item>

        <a-form-item label="分类">
          <a-select v-model:value="form.category" placeholder="请选择分类" allow-clear>
            <a-select-option value="TECH">技术交流</a-select-option>
            <a-select-option value="QNA">问答求助</a-select-option>
            <a-select-option value="SHARE">经验分享</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="标签">
          <a-select
            v-model:value="tagValues"
            mode="tags"
            placeholder="输入后回车添加标签"
            :token-separators="[',',';',' ']"
            :max-tag-count="5"
          />
        </a-form-item>

        <a-form-item label="内容" required>
          <a-textarea v-model:value="form.content" :rows="12" placeholder="请输入正文（支持换行）" />
        </a-form-item>

        <a-form-item label="图片">
          <a-upload
            list-type="picture-card"
            :file-list="fileList"
            :before-upload="beforeUpload"
            :custom-request="dummyRequest"
            @remove="onRemove"
          >
            <div>
              <PlusOutlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
          <div class="upload-tips">仅演示前端预览，实际上传接口待接入</div>
        </a-form-item>

        <a-form-item :wrapper-col="{ span: 20, offset: 4 }">
          <a-space>
            <a-button @click="goBack">返回</a-button>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">发布</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message, Upload } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import * as fileController from '@/api/fileController'
import * as postController from '@/api/postController'

const router = useRouter()

const form = reactive({
  title: '',
  category: 'TECH',
  content: '',
})

const tagValues = ref<string[]>([])
const fileList = ref<any[]>([])
const uploadingMap = ref<Record<string, boolean>>({})
const submitting = ref(false)

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件')
    return Upload.LIST_IGNORE
  }
  const isLt3M = file.size / 1024 / 1024 < 3
  if (!isLt3M) {
    message.error('图片大小需小于 3MB')
    return Upload.LIST_IGNORE
  }
  return false
}

// 仅做本地预览占位，后续接入上传接口时替换
const dummyRequest = async (options: any) => {
  const { onSuccess, onError, file } = options
  try {
    uploadingMap.value[file.uid] = true
    const form = new FormData()
    form.append('file', file as File)
    const res = await fileController.uploadUsingPost(form)
    if (res?.data?.code === 0 && res.data.data && res.data.data.length) {
      const url = res.data.data[0]
      const item = { uid: file.uid, name: file.name, status: 'done', url }
      // 替换或追加
      const idx = fileList.value.findIndex((f) => f.uid === file.uid)
      if (idx >= 0) fileList.value.splice(idx, 1, item)
      else fileList.value = [...fileList.value, item]
      onSuccess({}, file)
    } else {
      onError(new Error('upload failed'))
    }
  } catch (e) {
    onError(e)
  } finally {
    uploadingMap.value[file.uid] = false
  }
}

const onRemove = (file: any) => {
  fileList.value = fileList.value.filter((f) => f.uid !== file.uid)
}

const validate = () => {
  if (!form.title.trim()) {
    message.warning('请输入标题')
    return false
  }
  if (!form.content.trim()) {
    message.warning('请输入正文内容')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validate()) return
  submitting.value = true
  try {
    const images = fileList.value
      .filter((f) => f.status === 'done' && f.url)
      .map((f) => f.url)
      .join(',')
    const tags = tagValues.value.join(',')
    const res = await postController.addPostUsingPost({
      title: form.title.trim(),
      content: form.content.trim(),
      category: form.category,
      tags,
      images,
    })
    if (res?.data?.code === 0 && res.data.data) {
      message.success('发布成功')
      router.replace(`/post/${res.data.data}`)
    } else {
      message.error(res?.data?.message || '发布失败，请稍后重试')
    }
  } catch (e) {
    message.error('发布失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const goBack = () => router.back()
</script>

<style scoped lang="scss">
.post-create-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;

  .create-card {
    border-radius: 12px;
  }

  .upload-tips {
    color: #8c8c8c;
    font-size: 12px;
    margin-top: 8px;
  }
}
</style>



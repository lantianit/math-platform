<template>
  <div class="user-edit-page">
    <a-card title="编辑个人资料">
      <a-form :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }" @submit.prevent>
        <a-form-item label="头像">
          <a-upload
            list-type="picture-card"
            :max-count="1"
            :file-list="fileList"
            :before-upload="beforeUpload"
            :custom-request="customUpload"
            @remove="onRemove"
            accept="image/*"
          >
            <div>
              <PlusOutlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="昵称">
          <a-input v-model:value="form.userName" placeholder="请输入昵称" :maxlength="20" />
        </a-form-item>

        <a-form-item label="个人简介">
          <a-textarea v-model:value="form.userProfile" :rows="4" placeholder="一句话介绍自己" :maxlength="120" />
        </a-form-item>

        <a-form-item :wrapper-col="{ span: 20, offset: 4 }">
          <a-space>
            <a-button @click="goBack">返回</a-button>
            <a-button type="primary" :loading="saving" @click="save">保存</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message, Upload } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import * as userController from '@/api/userController'
import * as fileController from '@/api/fileController'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const form = reactive({
  userName: '',
  userProfile: '',
  userAvatar: '',
})

const fileList = ref<any[]>([])
const saving = ref(false)

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
  // 通过校验，交给 customRequest 处理
  return true
}

const customUpload = async (options: any) => {
  const { onSuccess, onError, onProgress, file } = options
  try {
    // 先放入占位项，展示上传中
    fileList.value = [{ uid: file.uid, name: file.name, status: 'uploading', percent: 30 }]
    if (onProgress) onProgress({ percent: 30 })
    const res = await fileController.uploadAvatarPendingUsingPost({}, file as File)
    if (res?.data?.code === 0 && res.data.data) {
      const data = res.data.data
      const url = data.url || data.thumbnailUrl
      if (!url) throw new Error('no url')
      // 不直接更新用户头像，提交审核
      const submit = await userController.submitAvatarReviewUsingPost({
        objectKey: data.objectKey,
        url: data.url,
        thumbnailKey: data.thumbnailKey,
        thumbnailUrl: data.thumbnailUrl,
        etag: data.etag,
        sha256: data.sha256,
        width: data.picWidth,
        height: data.picHeight,
        format: data.picFormat,
        size: data.picSize,
      } as any)
      if (submit?.data?.code !== 0) {
        throw new Error(submit?.data?.message || 'submit failed')
      }
      fileList.value = [{ uid: file.uid, name: file.name, status: 'done', url, percent: 100 }]
      if (onProgress) onProgress({ percent: 100 })
      onSuccess({ url }, file)
      message.success('头像已提交，等待管理员审核')
    } else {
      message.error(res?.data?.message || '上传失败')
      onError(new Error(res?.data?.message || 'upload failed'))
    }
  } catch (e) {
    message.error('上传失败')
    onError(e)
  }
}

const onRemove = () => {
  fileList.value = []
  form.userAvatar = ''
}

const load = async () => {
  await loginUserStore.fetchLoginUser()
  const u = loginUserStore.loginUser
  form.userName = u?.userName || ''
  form.userProfile = (u as any)?.userProfile || ''
  form.userAvatar = (u as any)?.userAvatar || ''
  if (form.userAvatar) {
    fileList.value = [{ uid: 'init', name: 'avatar', status: 'done', url: form.userAvatar }]
  }
}

const save = async () => {
  if (!form.userName.trim()) {
    message.warning('请输入昵称')
    return
  }
  saving.value = true
  try {
    const res = await userController.updateProfileUsingPost({
      userName: form.userName.trim(),
      userProfile: form.userProfile.trim(),
    } as any)
    if (res?.data?.code === 0) {
      message.success('保存成功')
      await loginUserStore.fetchLoginUser()
      router.back()
    } else {
      message.error(res?.data?.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

const goBack = () => router.back()

onMounted(() => {
  load()
})
</script>

<style scoped lang="scss">
.user-edit-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
</style>



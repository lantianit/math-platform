<template>
  <div class="add-note-space-page">
    <a-card :title="isEdit ? '编辑笔记空间' : '创建笔记空间'">
      <a-form
        :model="formState"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        @finish="onFinish"
      >
        <a-form-item
          label="空间名称"
          name="spaceName"
          :rules="[{ required: true, message: '请输入空间名称' }]"
        >
          <a-input
            v-model:value="formState.spaceName"
            placeholder="请输入空间名称"
            :maxlength="50"
          />
        </a-form-item>

        <a-form-item
          label="空间级别"
          name="spaceLevel"
          :rules="[{ required: true, message: '请选择空间级别' }]"
        >
          <a-select
            v-model:value="formState.spaceLevel"
            placeholder="请选择空间级别"
            @change="onLevelChange"
          >
            <a-select-option
              v-for="option in spaceLevelOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="配额说明" v-if="currentLevelInfo">
          <a-descriptions bordered size="small" :column="1">
            <a-descriptions-item label="最大笔记数">
              {{ currentLevelInfo.maxCount }} 篇
            </a-descriptions-item>
            <a-descriptions-item label="最大存储空间">
              {{ formatSize(currentLevelInfo.maxSize) }}
            </a-descriptions-item>
          </a-descriptions>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
          <a-space>
            <a-button type="primary" html-type="submit" :loading="submitting">
              {{ isEdit ? '保存' : '创建' }}
            </a-button>
            <a-button @click="goBack">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  addNoteSpaceUsingPost,
  editNoteSpaceUsingPost,
  getNoteSpaceVoByIdUsingGet,
  listNoteSpaceLevelUsingGet,
} from '@/api/noteSpaceController'
import { NOTE_SPACE_LEVEL_OPTIONS, formatSize } from '@/constants/noteSpace'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)
const spaceId = computed(() => Number(route.params.id))

const submitting = ref(false)
const spaceLevelOptions = ref(NOTE_SPACE_LEVEL_OPTIONS)
const levelInfoList = ref<API.NoteSpaceLevel[]>([])

const formState = reactive({
  spaceName: '',
  spaceLevel: 0,
})

// 当前选中的级别信息
const currentLevelInfo = computed(() => {
  return levelInfoList.value.find((item) => item.value === formState.spaceLevel)
})

// 获取级别列表
const loadLevelList = async () => {
  try {
    const res = await listNoteSpaceLevelUsingGet()
    if (res.data.code === 0 && res.data.data) {
      levelInfoList.value = res.data.data
    }
  } catch (error) {
    console.error('加载级别列表失败', error)
  }
}

// 加载空间详情（编辑时）
const loadSpaceDetail = async () => {
  if (!isEdit.value) return

  try {
    const res = await getNoteSpaceVoByIdUsingGet({ id: spaceId.value })
    if (res.data.code === 0 && res.data.data) {
      formState.spaceName = res.data.data.spaceName || ''
      formState.spaceLevel = res.data.data.spaceLevel || 0
    }
  } catch (error) {
    message.error('加载失败')
    goBack()
  }
}

// 级别变化
const onLevelChange = (value: number) => {
  formState.spaceLevel = value
}

// 提交表单
const onFinish = async () => {
  submitting.value = true
  try {
    if (isEdit.value) {
      const res = await editNoteSpaceUsingPost({
        id: spaceId.value,
        ...formState,
      })
      if (res.data.code === 0) {
        message.success('保存成功')
        goBack()
      } else {
        message.error(res.data.message || '保存失败')
      }
    } else {
      const res = await addNoteSpaceUsingPost(formState)
      if (res.data.code === 0) {
        message.success('创建成功')
        goBack()
      } else {
        message.error(res.data.message || '创建失败')
      }
    }
  } catch (error) {
    message.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadLevelList()
  if (isEdit.value) {
    loadSpaceDetail()
  }
})
</script>

<style scoped lang="scss">
.add-note-space-page {
  max-width: 800px;
  margin: 0 auto;
}
</style>


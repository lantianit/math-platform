<template>
  <div class="add-note-page">
    <a-card :title="isEdit ? '编辑笔记' : '创建笔记'">
      <a-form
        :model="formState"
        :label-col="{ span: 2 }"
        :wrapper-col="{ span: 20 }"
        @finish="onFinish"
      >
        <a-form-item
          label="标题"
          name="title"
          :rules="[{ required: true, message: '请输入笔记标题' }]"
        >
          <a-input
            v-model:value="formState.title"
            placeholder="请输入笔记标题"
            :maxlength="200"
          />
        </a-form-item>

        <a-form-item label="所属空间" name="spaceId">
          <a-select
            v-model:value="formState.spaceId"
            placeholder="选择私人空间（留空为公共笔记）"
            allow-clear
          >
            <a-select-option
              v-for="space in spaceList"
              :key="space.id"
              :value="space.id"
            >
              {{ space.spaceName }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          label="分类"
          name="category"
          :rules="[{ required: true, message: '请输入分类' }]"
        >
          <a-input
            v-model:value="formState.category"
            placeholder="例如：学习、工作、生活"
            :maxlength="50"
          />
        </a-form-item>

        <a-form-item label="标签" name="tags">
          <a-select
            v-model:value="formState.tags"
            mode="tags"
            placeholder="输入标签后按回车添加"
            :max-tag-count="10"
          />
        </a-form-item>

        <a-form-item
          label="内容"
          name="content"
          :rules="[{ required: true, message: '请输入笔记内容' }]"
        >
          <RichMarkdownEditor v-model:value="formState.content" />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formState.status">
            <a-radio :value="0">草稿</a-radio>
            <a-radio :value="1">发布</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 2, span: 20 }">
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
import RichMarkdownEditor from '@/components/RichMarkdownEditor.vue'
import {
  addNoteUsingPost,
  editNoteUsingPost,
  getNoteVoByIdUsingGet,
} from '@/api/noteController'
import { listNoteSpaceVoByPageUsingPost } from '@/api/noteSpaceController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

const router = useRouter()
const route = useRoute()
const loginUserStore = useLoginUserStore()

const isEdit = computed(() => !!route.params.id)
const noteId = computed(() => Number(route.params.id))
const querySpaceId = computed(() =>
  route.query.spaceId ? Number(route.query.spaceId) : undefined
)

const submitting = ref(false)
const spaceList = ref<API.NoteSpaceVO[]>([])

const formState = reactive({
  title: '',
  spaceId: querySpaceId.value,
  category: 'study',
  tags: [] as string[],
  content: '',
  status: 1,
})

// 加载笔记空间列表
const loadSpaceList = async () => {
  try {
    const res = await listNoteSpaceVoByPageUsingPost({
      userId: loginUserStore.loginUser?.id,
      current: 1,
      pageSize: 20,
    })
    if (res.data.code === 0) {
      spaceList.value = res.data.data?.records || []
    }
  } catch (error) {
    console.error('加载空间列表失败', error)
  }
}

// 加载笔记详情（编辑时）
const loadNoteDetail = async () => {
  if (!isEdit.value) return

  try {
    const res = await getNoteVoByIdUsingGet({ id: noteId.value })
    if (res.data.code === 0 && res.data.data) {
      const note = res.data.data
      formState.title = note.title || ''
      formState.spaceId = note.spaceId
      formState.category = note.category || 'study'
      formState.tags = note.tags ? JSON.parse(note.tags) : []
      formState.content = note.content || ''
      formState.status = note.status || 1
    }
  } catch (error) {
    message.error('加载失败')
    goBack()
  }
}

// 提交表单
const onFinish = async () => {
  submitting.value = true
  try {
    const submitData = {
      ...formState,
      tags: JSON.stringify(formState.tags),
    }

    if (isEdit.value) {
      const res = await editNoteUsingPost({
        id: noteId.value,
        ...submitData,
      })
      if (res.data.code === 0) {
        message.success('保存成功')
        goBack()
      } else {
        message.error(res.data.message || '保存失败')
      }
    } else {
      const res = await addNoteUsingPost(submitData)
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
  loadSpaceList()
  if (isEdit.value) {
    loadNoteDetail()
  }
})
</script>

<style scoped lang="scss">
.add-note-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>


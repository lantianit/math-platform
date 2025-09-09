<template>
  <div class="rich-md-editor">
    <div class="toolbar">
      <a-space>
        <a-button size="small" @click="wrap('**','**')">B</a-button>
        <a-button size="small" @click="wrap('*','*')"><i>I</i></a-button>
        <a-button size="small" @click="insert('# ')">H1</a-button>
        <a-button size="small" @click="insert('## ')">H2</a-button>
        <a-button size="small" @click="insert('> ')">引用</a-button>
        <a-button size="small" @click="wrap('`','`')">代码</a-button>
        <a-button size="small" @click="wrap('$$\n','\n$$')">公式</a-button>
        <a-button size="small" @click="togglePreview">{{ preview ? '编辑' : '预览' }}</a-button>
      </a-space>
    </div>
    <div v-if="!preview" class="editor">
      <a-textarea
        v-model:value="localValue"
        :rows="rows"
        placeholder="支持 Markdown 与 $...$/$$...$$ 数学公式"
        @paste="onPaste"
      />
      <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="onPickImage" />
      <div class="uploader">
        <a-button size="small" @click="triggerPick">插入图片</a-button>
      </div>
    </div>
    <div v-else class="preview" v-html="rendered"></div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { renderMarkdownWithKatex } from '@/utils/markdown'

interface Props {
  modelValue: string
  rows?: number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  rows: 16,
})

const emit = defineEmits<{ 'update:modelValue': [val: string] }>()

const localValue = ref(props.modelValue)
watch(() => props.modelValue, (v) => { if (v !== localValue.value) localValue.value = v })
watch(localValue, (v) => emit('update:modelValue', v))

const preview = ref(false)
const rendered = computed(() => renderMarkdownWithKatex(localValue.value))

const togglePreview = () => { preview.value = !preview.value }

function insert(prefix: string, suffix = '') {
  const start = prefix
  localValue.value = start + localValue.value + suffix
}

function wrap(prefix: string, suffix: string) {
  const v = localValue.value
  localValue.value = `${prefix}${v}${suffix}`
}

// 图片上传与粘贴
const fileInput = ref<HTMLInputElement | null>(null)
function triggerPick() { fileInput.value?.click() }

async function onPickImage(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input?.files?.[0]
  if (!file) return
  await uploadAndInsert(file)
  input.value = ''
}

async function onPaste(e: ClipboardEvent) {
  const item = e.clipboardData?.items?.[0]
  if (!item) return
  if (item.type.startsWith('image/')) {
    e.preventDefault()
    const file = item.getAsFile()
    if (file) await uploadAndInsert(file)
  }
}

async function uploadAndInsert(file: File) {
  try {
    const mod = await import('@/api/fileController')
    const res = await (mod as any).uploadUsingPost({ file: [file] })
    const url = res?.data?.data?.[0]
    if (url) {
      const md = `\n![image](${url})\n`
      localValue.value += md
    }
  } catch {}
}
</script>

<style scoped>
.rich-md-editor .toolbar {
  margin-bottom: 8px;
}
.rich-md-editor .editor .ant-input-textarea {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
}
.rich-md-editor .preview {
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  padding: 12px;
  background: #fff;
}
</style>



import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
// 移除vue-cropper相关导入，暂时不需要
import 'ant-design-vue/dist/reset.css'
import 'katex/dist/katex.min.css'
import 'highlight.js/styles/github.min.css'
import '@/styles/main.scss'
import { applyDesignTokens } from '@/styles/tokens/design-tokens'
import '@/access.ts'

// 应用企业级设计token
applyDesignTokens()

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
// app.use(VueCropper)

app.mount('#app')

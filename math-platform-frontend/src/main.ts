import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
// 移除vue-cropper相关导入，暂时不需要
import 'ant-design-vue/dist/reset.css'
import '@/access.ts'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
// app.use(VueCropper)

app.mount('#app')

<template>
  <div id="homePage">
    <div class="welcome-section">
      <h1>数学刷题平台</h1>
      <p class="subtitle">数学刷题平台</p>

      <!-- 未登录状态显示登录提示 -->
      <div v-if="!loginUserStore.loginUser.id" class="login-prompt">
        <p>请先登录以使用完整功能</p>
        <a-button type="primary" size="large" @click="goToLogin">
          立即登录
        </a-button>
      </div>

      <!-- 已登录状态显示用户信息 -->
      <div v-else class="user-welcome">
        <a-avatar :size="64" :src="loginUserStore.loginUser.userAvatar" />
        <h3>欢迎回来，{{ loginUserStore.loginUser.userName || '用户' }}！</h3>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { useRouter } from 'vue-router'

const loginUserStore = useLoginUserStore()
const router = useRouter()

// 跳转到登录页面
const goToLogin = () => {
  router.push('/user/login')
}
</script>

<style scoped>
#homePage {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-section {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 16px;
  margin-bottom: 40px;
}

.welcome-section h1 {
  font-size: 2.5rem;
  margin-bottom: 16px;
  font-weight: 600;
}

.subtitle {
  font-size: 1.2rem;
  margin-bottom: 32px;
  opacity: 0.9;
}

.login-prompt {
  margin-top: 32px;
}

.login-prompt p {
  margin-bottom: 20px;
  font-size: 1.1rem;
}

.user-welcome {
  margin-top: 32px;
}

.user-welcome h3 {
  margin: 16px 0 8px 0;
  font-size: 1.5rem;
}

.features-section {
  padding: 40px 0;
}

.features-section h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 40px;
  color: #333;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.feature-card {
  background: white;
  padding: 32px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-card h3 {
  color: #333;
  margin-bottom: 16px;
  font-size: 1.3rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}
</style>

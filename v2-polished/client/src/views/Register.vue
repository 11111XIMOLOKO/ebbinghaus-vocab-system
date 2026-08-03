<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <div class="auth-title">
        <div class="brand-logo" style="margin:0 auto">E</div>
        <h2>创建账号</h2>
        <p>开始你的艾宾浩斯背单词之旅</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（2-50 字符）" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="密码（至少 4 位）" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="确认密码" />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleRegister">
          {{ loading ? '注册中...' : '注 册' }}
        </el-button>
      </el-form>
      <div style="margin-top:18px;text-align:center">
        <span class="muted">已有账号？</span>
        <el-button type="primary" link @click="$router.push('/login')">返回登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '' })

const validateConfirm = (_r: any, v: string, cb: any) =>
  cb(v !== form.password ? new Error('两次密码不一致') : undefined)

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度需在 2-50 之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, message: '密码至少 4 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({ username: form.username, password: form.password })
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (e: any) { ElMessage.error(e.response?.data?.message || '注册失败') }
  finally { loading.value = false }
}
</script>

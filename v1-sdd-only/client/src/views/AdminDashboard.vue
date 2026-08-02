<template>
  <div class="admin-container">
    <el-container>
      <el-header class="page-header">
        <h2>🛡️ 后台管理</h2>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>
      <el-main>
        <el-tabs type="border-card">
          <!-- 用户管理 -->
          <el-tab-pane label="用户管理">
            <el-table :data="users" stripe v-loading="uload">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" />
              <el-table-column prop="role" label="角色" width="100" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status===1 ? 'success' : 'danger'">
                    {{ row.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160">
                <template #default="{ row }">
                  <el-button size="small" @click="toggleUser(row)">切换</el-button>
                  <el-button size="small" type="danger" @click="delUser(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 公告管理 -->
          <el-tab-pane label="公告管理">
            <el-button type="primary" @click="showAnnDialog = true" style="margin-bottom:12px">新建公告</el-button>
            <el-table :data="announcements" stripe>
              <el-table-column prop="title" label="标题" />
              <el-table-column prop="type" label="类型" width="100" />
              <el-table-column prop="createTime" label="时间" width="180" />
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button size="small" type="danger" @click="delAnn(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 操作日志 -->
          <el-tab-pane label="操作日志">
            <el-table :data="logs" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="module" label="模块" width="100" />
              <el-table-column prop="requestMethod" label="方法" width="80" />
              <el-table-column prop="requestUri" label="路径" />
              <el-table-column prop="createTime" label="时间" width="180" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>

    <!-- 新建公告弹窗 -->
    <el-dialog v-model="showAnnDialog" title="新建公告" width="500px">
      <el-form :model="annForm">
        <el-form-item label="标题"><el-input v-model="annForm.title" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="annForm.type">
            <el-option label="通知" value="NOTICE" />
            <el-option label="公告" value="GENERAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="annForm.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAnnDialog = false">取消</el-button>
        <el-button type="primary" @click="createAnn">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const uload = ref(false)
const users = ref<any[]>([])
const announcements = ref<any[]>([])
const logs = ref<any[]>([])
const showAnnDialog = ref(false)
const annForm = reactive({ title: '', type: 'NOTICE', content: '' })

async function loadUsers() {
  const res = await request.get('/admin/users')
  users.value = res.data.data.records
}
async function toggleUser(row: any) {
  await request.put(`/admin/users/${row.id}/toggle`)
  ElMessage.success('状态已切换')
  loadUsers()
}
async function delUser(row: any) {
  await request.delete(`/admin/users/${row.id}`)
  ElMessage.success('已删除')
  loadUsers()
}
async function loadAnn() {
  const res = await request.get('/admin/announcements')
  announcements.value = res.data.data.records
}
async function createAnn() {
  await request.post('/admin/announcements', annForm)
  ElMessage.success('公告已发布')
  showAnnDialog.value = false
  annForm.title = ''; annForm.content = ''
  loadAnn()
}
async function delAnn(row: any) {
  await request.delete(`/admin/announcements/${row.id}`)
  ElMessage.success('已删除')
  loadAnn()
}
async function loadLogs() {
  const res = await request.get('/admin/operation-logs')
  logs.value = res.data.data.records
}

onMounted(() => { loadUsers(); loadAnn(); loadLogs() })
</script>

<style scoped>
.admin-container { min-height: 100vh; background: #f5f7fa; }
.page-header { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 0 24px; height: 64px; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
.page-header h2 { margin: 0; }
</style>

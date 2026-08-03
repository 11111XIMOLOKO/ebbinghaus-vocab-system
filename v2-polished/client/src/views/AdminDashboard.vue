<template>
  <div class="page-grid">
    <el-card class="stat-card">
      <el-tabs v-model="tab" type="border-card">
        <el-tab-pane label="用户管理" name="users">
          <el-table :data="users" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="role" label="角色" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{row.status===1?'正常':'禁用'}}</el-tag></template>
            </el-table-column>
            <el-table-column label="操作" width="140" align="center">
              <template #default="{row}">
                <el-button size="small" :type="row.status===1?'warning':'success'" @click="toggleUser(row)">{{row.status===1?'禁用':'启用'}}</el-button>
                <el-button size="small" type="danger" @click="delUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination style="margin-top:16px;justify-content:center;display:flex" v-model:current-page="uPage" :total="uTotal" :page-size="20" layout="total,prev,pager,next" @current-change="loadUsers" />
        </el-tab-pane>

        <el-tab-pane label="公告管理" name="anns">
          <el-button type="primary" @click="annDialog=true" style="margin-bottom:12px">新建公告</el-button>
          <el-table :data="anns" stripe>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'已发布':'草稿'}}</el-tag></template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180" />
            <el-table-column label="操作" width="100" align="center">
              <template #default="{row}"><el-button size="small" type="danger" @click="delAnn(row)">删除</el-button></template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="操作日志" name="logs">
          <el-table :data="logs" stripe>
            <el-table-column prop="module" label="模块" width="120" />
            <el-table-column prop="requestMethod" label="方法" width="80" />
            <el-table-column prop="requestUri" label="路径" />
            <el-table-column prop="ip" label="IP" width="140" />
            <el-table-column prop="createTime" label="时间" width="180" />
          </el-table>
          <el-pagination style="margin-top:16px;justify-content:center;display:flex" v-model:current-page="lPage" :total="lTotal" :page-size="20" layout="total,prev,pager,next" @current-change="loadLogs" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="annDialog" title="新建公告" width="480px">
      <el-form :model="annForm" label-width="60px">
        <el-form-item label="标题"><el-input v-model="annForm.title" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="annForm.type" placeholder="NOTICE" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="annForm.content" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="annDialog=false">取消</el-button>
        <el-button type="primary" :loading="annSub" @click="createAnn">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const tab = ref('users')
const users = ref<any[]>([]); const uPage = ref(1); const uTotal = ref(0)
const anns = ref<any[]>([]); const logs = ref<any[]>([]); const lPage = ref(1); const lTotal = ref(0)
const annDialog = ref(false); const annSub = ref(false)
const annForm = ref({ title:'', content:'', type:'NOTICE' })

async function loadUsers() {
  const r = await request.get('/admin/users', { params: { pageNum: uPage.value } })
  users.value = r.data.data.records; uTotal.value = r.data.data.total
}
async function toggleUser(row: any) {
  await request.put(`/admin/users/${row.id}/toggle`); ElMessage.success(row.status===1?'已禁用':'已启用'); loadUsers()
}
async function delUser(row: any) {
  await ElMessageBox.confirm(`确定删除 ${row.username}？`,'确认'); await request.delete(`/admin/users/${row.id}`); ElMessage.success('已删除'); loadUsers()
}
async function loadAnns() { const r = await request.get('/admin/announcements'); anns.value = r.data.data.records }
async function createAnn() {
  annSub.value = true
  try { await request.post('/admin/announcements', annForm.value); ElMessage.success('已发布'); annDialog.value=false; loadAnns() }
  catch(e:any) { ElMessage.error(e.response?.data?.message||'失败') }
  finally { annSub.value = false }
}
async function delAnn(row: any) { await ElMessageBox.confirm('确定删除？','确认'); await request.delete(`/admin/announcements/${row.id}`); loadAnns() }
async function loadLogs() {
  const r = await request.get('/admin/operation-logs', { params: { pageNum: lPage.value } })
  logs.value = r.data.data.records; lTotal.value = r.data.data.total
}
onMounted(() => { loadUsers(); loadAnns(); loadLogs() })
</script>

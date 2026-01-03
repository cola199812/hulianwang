<template>
  <div class="p-4" style="max-width: 1200px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 20px;">
      <h1 class="text-xl font-bold mb-4">装备服务</h1>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
    
    <!-- 标签页切换不同功能模块 -->
    <el-tabs v-model="activeTab" type="card">
      <!-- 装备租赁模块 -->
      <el-tab-pane label="装备租赁" name="rental">
        <div class="mb-4">
          <el-select v-model="rentalFilter" placeholder="按类型筛选" clearable style="width: 200px; margin-right: 10px;" @change="loadRentalEquipments">
            <el-option label="所有类型" value="" />
            <el-option label="帐篷" value="帐篷" />
            <el-option label="睡袋" value="睡袋" />
            <el-option label="器材" value="器材" />
            <el-option label="照明" value="照明" />
            <el-option label="鞋靴" value="鞋靴" />
            <el-option label="服装" value="服装" />
          </el-select>
        </div>
        <div class="gear-grid">
          <div v-for="gear in rentalEquipments" :key="gear.id" class="gear-card">
            <div class="gear-cover">{{ (gear.name || '').slice(0,1) }}</div>
            <div class="gear-title">{{ gear.name }}</div>
            <div class="gear-meta">{{ gear.type }}</div>
            <div class="gear-price">
              <span class="daily-price">¥{{ gear.dailyRentalPrice }}/天</span>
              <span class="deposit">押金: ¥{{ gear.deposit }}</span>
            </div>
            <div class="gear-stock">库存: {{ gear.stock }}件</div>
            <el-button type="primary" size="small" @click="openRentalDialog(gear)" :disabled="gear.stock <= 0">
              {{ gear.stock <= 0 ? '库存不足' : '立即租赁' }}
            </el-button>
          </div>
        </div>
        <div v-if="rentalEquipments.length === 0" class="no-data">
          <el-empty description="暂无可租赁装备" />
        </div>
      </el-tab-pane>

      <!-- 智能装备清单模块 -->
      <el-tab-pane label="智能清单" name="checklist">
        <div class="mb-4">
          <el-button type="primary" @click="openSmartChecklistDialog">生成智能清单</el-button>
        </div>
        <el-card v-if="checklists.length === 0" class="no-checklist">
          <el-empty description="暂无装备清单，点击上方按钮生成" />
        </el-card>
        <div v-else class="checklist-list">
          <el-card v-for="checklist in checklists" :key="checklist.id" class="checklist-card">
            <div class="checklist-header">
              <div class="checklist-name">{{ checklist.name }}</div>
              <div class="checklist-info">
                <span class="info-item">路线: {{ checklist.routeType }}</span>
                <span class="info-item">天气: {{ checklist.weather }}</span>
              </div>
            </div>
            <div class="checklist-items">
              <div v-for="item in checklist.items" :key="item.id" class="checklist-item">
                <el-checkbox v-model="item.isPacked" @change="updateItemPacked(item)">{{ item.itemName }}</el-checkbox>
                <div class="item-quantity">x{{ item.quantity }}</div>
              </div>
            </div>
            <div class="checklist-actions">
              <el-button size="small">导出清单</el-button>
              <el-button size="small" type="danger" @click="deleteChecklist(checklist.id)">删除</el-button>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 个人装备管理模块 -->
      <el-tab-pane label="个人装备" name="personal">
        <div class="mb-4">
          <el-button type="primary" @click="openAddPersonalDialog">添加装备</el-button>
          <el-button @click="showMaintenanceReminders" style="margin-left: 10px;">
            维护提醒 <el-badge v-if="maintenanceCount > 0" :value="maintenanceCount" type="danger" />
          </el-button>
        </div>
        <div class="personal-equipment-grid">
          <div v-for="equipment in personalEquipments" :key="equipment.id" class="personal-equipment-card">
            <div class="gear-cover">{{ (equipment.name || '').slice(0,1) }}</div>
            <div class="gear-title">{{ equipment.name }}</div>
            <div class="gear-meta">{{ equipment.type }}</div>
            <div class="personal-equipment-info">
              <div class="info-item">使用次数: {{ equipment.usageCount }}</div>
              <div class="info-item" :class="{ 'need-maintenance': isNeedMaintenance(equipment) }">
                下次维护: {{ equipment.nextMaintenanceDate }}
              </div>
            </div>
            <div class="equipment-actions">
              <el-button size="small" @click="increaseUsageCount(equipment.id)">增加使用</el-button>
              <el-button size="small" type="danger" @click="deletePersonalEquipment(equipment.id)">删除</el-button>
            </div>
          </div>
        </div>
        <div v-if="personalEquipments.length === 0" class="no-data">
          <el-empty description="暂无个人装备" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 租赁对话框 -->
    <el-dialog v-model="rentalDialogVisible" title="装备租赁" width="500px">
      <el-form :model="rentalForm" label-width="100px">
          <el-form-item label="装备名称">
            <el-input v-model="rentalForm.name" readonly />
          </el-form-item>
          <el-form-item label="每日租金">
            <el-input v-model="rentalForm.dailyPrice" readonly />
          </el-form-item>
          <el-form-item label="押金">
            <el-input v-model="rentalForm.deposit" readonly />
          </el-form-item>
          <el-form-item label="租赁开始日期">
            <el-date-picker v-model="rentalForm.startDate" type="date" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="租赁结束日期">
            <el-date-picker v-model="rentalForm.endDate" type="date" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="rentalForm.phone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rentalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRental">确认租赁</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 智能清单生成对话框 -->
    <el-dialog v-model="smartChecklistDialogVisible" title="生成智能清单" width="400px">
      <el-form :model="smartChecklistForm" label-width="100px">
        <el-form-item label="清单名称">
          <el-input v-model="smartChecklistForm.name" placeholder="输入清单名称" />
        </el-form-item>
        <el-form-item label="路线类型">
          <el-select v-model="smartChecklistForm.routeType" placeholder="选择路线类型" style="width: 100%;">
            <el-option label="山地徒步" value="山地徒步" />
            <el-option label="森林探险" value="森林探险" />
            <el-option label="沙漠穿越" value="沙漠穿越" />
            <el-option label="水上活动" value="水上活动" />
            <el-option label="城市徒步" value="城市徒步" />
            <el-option label="露营" value="露营" />
          </el-select>
        </el-form-item>
        <el-form-item label="天气情况">
          <el-select v-model="smartChecklistForm.weather" placeholder="选择天气情况" style="width: 100%;">
            <el-option label="晴天" value="晴天" />
            <el-option label="雨天" value="雨天" />
            <el-option label="雪天" value="雪天" />
            <el-option label="多云" value="多云" />
            <el-option label="高温" value="高温" />
            <el-option label="低温" value="低温" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="smartChecklistDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="generateSmartChecklist">生成清单</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加个人装备对话框 -->
    <el-dialog v-model="addPersonalDialogVisible" title="添加个人装备" width="400px">
      <el-form :model="addPersonalForm" label-width="100px">
        <el-form-item label="装备名称">
          <el-input v-model="addPersonalForm.name" placeholder="输入装备名称" />
        </el-form-item>
        <el-form-item label="装备类型">
          <el-select v-model="addPersonalForm.type" placeholder="选择装备类型" style="width: 100%;">
            <el-option label="帐篷" value="帐篷" />
            <el-option label="睡袋" value="睡袋" />
            <el-option label="器材" value="器材" />
            <el-option label="照明" value="照明" />
            <el-option label="鞋靴" value="鞋靴" />
            <el-option label="服装" value="服装" />
          </el-select>
        </el-form-item>
        <el-form-item label="购买日期">
          <el-date-picker v-model="addPersonalForm.purchaseDate" type="date" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addPersonalForm.note" type="textarea" placeholder="添加备注信息" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addPersonalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addPersonalEquipment">确认添加</el-button>
        </div>
      </template>
    </el-dialog>
</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

// 使用封装的http实例
const axios = http

// 标签页状态
const activeTab = ref('rental')

// 装备租赁模块数据
const rentalFilter = ref('')
const rentalEquipments = ref([])

// 加载租赁装备列表
async function loadRentalEquipments() {
  try {
    let url = '/equipment/rental/list'
    if (rentalFilter.value) {
      url = `/equipment/rental/list/type/${rentalFilter.value}`
    }
    const response = await axios.get(url)
    rentalEquipments.value = response.data.equipments
  } catch (error) {
    ElMessage.error('加载租赁装备失败: ' + (error.response?.data?.message || error.message))
    console.error('加载租赁装备失败:', error)
  }
}

// 装备租赁对话框
const rentalDialogVisible = ref(false)
const rentalForm = ref({
  name: '',
  dailyPrice: '',
  deposit: '',
  startDate: '',
  endDate: '',
  phone: ''
})

// 智能清单模块数据
const checklists = ref([])
const smartChecklistDialogVisible = ref(false)
const smartChecklistForm = ref({
  name: '',
  routeType: '',
  weather: ''
})

// 个人装备管理模块数据
const personalEquipments = ref([])
const addPersonalDialogVisible = ref(false)
const addPersonalForm = ref({
  name: '',
  type: '',
  purchaseDate: '',
  note: ''
})

// 维护提醒数量
const maintenanceCount = computed(() => {
  return personalEquipments.value.filter(equipment => isNeedMaintenance(equipment)).length
})

// 加载数据
onMounted(() => {
  // 加载真实数据
  loadRentalEquipments()
  loadPersonalEquipments()
  loadChecklists()
})

// 装备租赁相关方法
function openRentalDialog(gear) {
  currentRentalGear.value = gear
  rentalForm.value = {
    name: gear.name,
    dailyPrice: gear.dailyRentalPrice,
    deposit: gear.deposit,
    startDate: '',
    endDate: '',
    phone: ''
  }
  rentalDialogVisible.value = true
}

async function confirmRental() {
  if (!rentalForm.value.startDate || !rentalForm.value.endDate) {
    ElMessage.warning('请选择租赁日期')
    return
  }
  if (new Date(rentalForm.value.startDate) > new Date(rentalForm.value.endDate)) {
    ElMessage.warning('结束日期不能早于开始日期')
    return
  }
  if (!rentalForm.value.phone) {
    ElMessage.warning('请输入联系电话')
    return
  }
  
  try {
    // 调用后端API创建租赁订单
    const response = await axios.post('/equipment/rental/create', {
      equipmentId: currentRentalGear.value.id,
      startTime: new Date(rentalForm.value.startDate).toISOString().split('T')[0] + 'T00:00:00',
      endTime: new Date(rentalForm.value.endDate).toISOString().split('T')[0] + 'T23:59:59',
      rentalDays: Math.ceil((new Date(rentalForm.value.endDate) - new Date(rentalForm.value.startDate)) / (1000 * 60 * 60 * 24)) + 1,
      contactPhone: rentalForm.value.phone
    })
    
    ElMessage.success('租赁申请提交成功，请完成支付')
    rentalDialogVisible.value = false
    // 刷新租赁装备列表
    loadRentalEquipments()
    // 刷新个人装备列表，显示刚租赁的装备
    loadPersonalEquipments()
  } catch (error) {
    ElMessage.error('租赁申请失败: ' + (error.response?.data?.message || error.message))
    console.error('租赁申请失败:', error)
  }
}

// 当前选中的租赁装备
const currentRentalGear = ref(null)

// 智能清单相关方法
function openSmartChecklistDialog() {
  smartChecklistForm.value = {
    name: '',
    routeType: '',
    weather: ''
  }
  smartChecklistDialogVisible.value = true
}

async function generateSmartChecklist() {
  if (!smartChecklistForm.value.name || !smartChecklistForm.value.routeType || !smartChecklistForm.value.weather) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    // 调用后端API生成智能清单
    const response = await axios.post('/equipment/checklist/smart', null, {
      params: {
        name: smartChecklistForm.value.name,
        routeType: smartChecklistForm.value.routeType,
        weather: smartChecklistForm.value.weather
      }
    })
    
    const newChecklist = response.data.checklist
    // 获取清单详情（包含items）
    const detailResponse = await axios.get(`/equipment/checklist/detail/${newChecklist.id}`)
    newChecklist.items = detailResponse.data.items
    
    checklists.value.unshift(newChecklist)
    ElMessage.success('智能清单生成成功')
    smartChecklistDialogVisible.value = false
  } catch (error) {
    ElMessage.error('智能清单生成失败: ' + (error.response?.data?.message || error.message))
    console.error('智能清单生成失败:', error)
  }
}

function generateChecklistItems(routeType, weather) {
  // 根据路线类型和天气生成装备列表
  const baseItems = []
  
  // 基础装备
  baseItems.push({ id: Date.now() + 1, customName: '背包', quantity: 1, isPacked: false })
  baseItems.push({ id: Date.now() + 2, customName: '水壶', quantity: 1, isPacked: false })
  baseItems.push({ id: Date.now() + 3, customName: '急救包', quantity: 1, isPacked: false })
  
  // 路线类型对应的装备
  if (routeType === '山地徒步') {
    baseItems.push({ id: Date.now() + 4, customName: '登山鞋', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 5, customName: '登山杖', quantity: 2, isPacked: false })
    baseItems.push({ id: Date.now() + 6, customName: '头灯', quantity: 1, isPacked: false })
  } else if (routeType === '森林探险') {
    baseItems.push({ id: Date.now() + 4, customName: '防水靴', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 5, customName: '防虫喷雾', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 6, customName: '指南针', quantity: 1, isPacked: false })
  } else if (routeType === '露营') {
    baseItems.push({ id: Date.now() + 4, customName: '帐篷', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 5, customName: '睡袋', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 6, customName: '露营灯', quantity: 1, isPacked: false })
  }
  
  // 天气对应的装备
  if (weather === '晴天') {
    baseItems.push({ id: Date.now() + 7, customName: '遮阳帽', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 8, customName: '防晒霜', quantity: 1, isPacked: false })
  } else if (weather === '雨天') {
    baseItems.push({ id: Date.now() + 7, customName: '雨衣', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 8, customName: '防水袋', quantity: 1, isPacked: false })
  } else if (weather === '低温') {
    baseItems.push({ id: Date.now() + 7, customName: '保暖衣', quantity: 1, isPacked: false })
    baseItems.push({ id: Date.now() + 8, customName: '手套', quantity: 1, isPacked: false })
  }
  
  return baseItems
}

async function updateItemPacked(item) {
  try {
    const response = await axios.put(`/equipment/checklist/item/packed/${item.id}`, null, {
      params: {
        isPacked: item.isPacked
      }
    })
    ElMessage.success(response.data.message)
  } catch (error) {
    ElMessage.error('状态更新失败: ' + (error.response?.data?.message || error.message))
    console.error('状态更新失败:', error)
    // 回滚状态
    item.isPacked = !item.isPacked
  }
}

async function deleteChecklist(id) {
  try {
    const response = await axios.delete(`/equipment/checklist/${id}`)
    checklists.value = checklists.value.filter(checklist => checklist.id !== id)
    ElMessage.success(response.data.message)
  } catch (error) {
    ElMessage.error('清单删除失败: ' + (error.response?.data?.message || error.message))
    console.error('清单删除失败:', error)
  }
}

// 个人装备管理相关方法
async function loadPersonalEquipments() {
  try {
    const response = await axios.get('/equipment/personal/list')
    personalEquipments.value = response.data.equipments
  } catch (error) {
    ElMessage.error('加载个人装备失败: ' + (error.response?.data?.message || error.message))
    console.error('加载个人装备失败:', error)
    // 使用模拟数据作为后备
    personalEquipments.value = [
      { id: 1, name: '我的登山鞋', type: '鞋靴', purchaseDate: '2023-01-15', usageCount: 12, nextMaintenanceDate: '2024-03-15', note: '日常徒步使用' },
      { id: 2, name: '我的背包', type: '器材', purchaseDate: '2023-03-20', usageCount: 8, nextMaintenanceDate: '2024-06-20', note: '容量60L' },
      { id: 3, name: '我的头灯', type: '照明', purchaseDate: '2023-05-10', usageCount: 5, nextMaintenanceDate: '2024-08-10', note: 'USB充电' }
    ]
  }
}

async function loadChecklists() {
  try {
    const response = await axios.get('/equipment/checklist/my')
    const checklistList = response.data.checklists
    
    // 获取每个清单的详细信息（包含items）
    const checklistDetails = await Promise.all(
      checklistList.map(async checklist => {
        const detailResponse = await axios.get(`/equipment/checklist/detail/${checklist.id}`)
        return {
          ...checklist,
          items: detailResponse.data.items
        }
      })
    )
    
    checklists.value = checklistDetails
  } catch (error) {
    ElMessage.error('加载装备清单失败: ' + (error.response?.data?.message || error.message))
    console.error('加载装备清单失败:', error)
    // 使用模拟数据作为后备
    checklists.value = [
      {
        id: 1,
        name: '周末徒步清单',
        routeType: '山地徒步',
        weather: '晴天',
        items: [
          { id: 101, customName: '背包', quantity: 1, isPacked: true },
          { id: 102, customName: '水壶', quantity: 1, isPacked: true },
          { id: 103, customName: '登山鞋', quantity: 1, isPacked: true },
          { id: 104, customName: '登山杖', quantity: 2, isPacked: false },
          { id: 105, customName: '头灯', quantity: 1, isPacked: false },
          { id: 106, customName: '遮阳帽', quantity: 1, isPacked: true }
        ]
      }
    ]
  }
}

function openAddPersonalDialog() {
  addPersonalForm.value = {
    name: '',
    type: '',
    purchaseDate: '',
    note: ''
  }
  addPersonalDialogVisible.value = true
}

async function addPersonalEquipment() {
  if (!addPersonalForm.value.name || !addPersonalForm.value.type) {
    ElMessage.warning('请填写装备名称和类型')
    return
  }
  
  try {
    const response = await axios.post('/equipment/personal/add', {
      name: addPersonalForm.value.name,
      type: addPersonalForm.value.type,
      purchaseDate: addPersonalForm.value.purchaseDate 
        ? new Date(addPersonalForm.value.purchaseDate).toISOString().split('T')[0]
        : new Date().toISOString().split('T')[0],
      note: addPersonalForm.value.note
    })
    
    personalEquipments.value.push(response.data.equipment)
    ElMessage.success(response.data.message)
    addPersonalDialogVisible.value = false
  } catch (error) {
    ElMessage.error('装备添加失败: ' + (error.response?.data?.message || error.message))
    console.error('装备添加失败:', error)
  }
}

async function increaseUsageCount(id) {
  try {
    const response = await axios.put(`/equipment/personal/usage/${id}`)
    const equipment = personalEquipments.value.find(item => item.id === id)
    if (equipment) {
      equipment.usageCount += 1
    }
    ElMessage.success(response.data.message)
  } catch (error) {
    ElMessage.error('使用次数更新失败: ' + (error.response?.data?.message || error.message))
    console.error('使用次数更新失败:', error)
  }
}

async function deletePersonalEquipment(id) {
  try {
    // 调用后端API删除个人装备
    const response = await axios.delete(`/equipment/personal/delete/${id}`)
    personalEquipments.value = personalEquipments.value.filter(item => item.id !== id)
    ElMessage.success(response.data.message)
  } catch (error) {
    ElMessage.error('装备删除失败: ' + (error.response?.data?.message || error.message))
    console.error('装备删除失败:', error)
  }
}

async function showMaintenanceReminders() {
  try {
    const response = await axios.get('/equipment/personal/maintenance')
    const maintenanceEquipments = response.data.equipments
    
    if (maintenanceEquipments.length === 0) {
      ElMessage.info('当前没有需要维护的装备')
      return
    }
    
    const equipmentNames = maintenanceEquipments.map(equipment => equipment.name).join('、')
    ElMessage.warning(`以下装备需要维护: ${equipmentNames}`)
  } catch (error) {
    ElMessage.error('获取维护提醒失败: ' + (error.response?.data?.message || error.message))
    console.error('获取维护提醒失败:', error)
    
    // 降级到前端过滤
    const maintenanceEquipments = personalEquipments.value.filter(equipment => isNeedMaintenance(equipment))
    if (maintenanceEquipments.length === 0) {
      ElMessage.info('当前没有需要维护的装备')
      return
    }
    
    const equipmentNames = maintenanceEquipments.map(equipment => equipment.name).join('、')
    ElMessage.warning(`以下装备需要维护: ${equipmentNames}`)
  }
}

function isNeedMaintenance(equipment) {
  // 简单判断：如果下次维护日期早于或等于今天，则需要维护
  const today = new Date().toISOString().split('T')[0]
  return equipment.nextMaintenanceDate && equipment.nextMaintenanceDate <= today
}
</script>

<style scoped>
.gear-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:16px; margin-top: 20px; }
.gear-card { border:1px solid #eee; border-radius:10px; padding:16px; display:flex; flex-direction:column; gap:8px; transition: all 0.3s ease; }
.gear-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); transform: translateY(-2px); }
.gear-cover { width:100%; height:120px; border-radius:8px; background:linear-gradient(135deg,#bdf,#7ac); color:#fff; display:flex; align-items:center; justify-content:center; font-size:28px; font-weight:700; }
.gear-title { font-weight:600; font-size:16px; }
.gear-meta { color:#666; font-size:14px; }
.gear-price { display:flex; flex-direction:column; gap:4px; margin-top:4px; }
.daily-price { font-size:16px; font-weight:600; color:#f56c6c; }
.deposit { font-size:12px; color:#909399; }
.gear-stock { font-size:12px; color:#67c23a; }
.personal-equipment-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:16px; margin-top: 20px; }
.personal-equipment-card { border:1px solid #eee; border-radius:10px; padding:16px; display:flex; flex-direction:column; gap:8px; transition: all 0.3s ease; }
.personal-equipment-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); transform: translateY(-2px); }
.personal-equipment-info { display:flex; flex-direction:column; gap:4px; margin-top:4px; }
.info-item { font-size:12px; color:#666; }
.need-maintenance { color:#f56c6c; font-weight:500; }
.equipment-actions { display:flex; gap:8px; margin-top:8px; }
.checklist-list { display:flex; flex-direction:column; gap:16px; margin-top:20px; }
.checklist-card { transition: all 0.3s ease; }
.checklist-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.checklist-header { display:flex; flex-direction:column; gap:4px; margin-bottom:16px; }
.checklist-name { font-size:18px; font-weight:600; }
.checklist-info { display:flex; gap:16px; font-size:14px; color:#666; }
.checklist-items { display:flex; flex-direction:column; gap:8px; margin-bottom:16px; }
.checklist-item { display:flex; justify-content:space-between; align-items:center; }
.item-quantity { font-size:14px; color:#666; }
.checklist-actions { display:flex; justify-content:flex-end; gap:8px; }
.no-data, .no-checklist { margin-top:40px; }
@media (max-width: 768px) { 
  .gear-grid, .personal-equipment-grid { grid-template-columns:repeat(2,1fr); } 
}
@media (max-width: 480px) { 
  .gear-grid, .personal-equipment-grid { grid-template-columns:1fr; } 
}
</style>
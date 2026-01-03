<template>
  <div class="p-4 tools-page">
    <!-- 头部 -->
    <div class="page-header">
      <h1 class="page-title">🧰 工具服务</h1>
      <el-button type="success" @click="$router.push('/')">
        <el-icon><HomeFilled /></el-icon>返回首页
      </el-button>
    </div>

    <el-tabs v-model="activeTab" type="border-card" class="custom-tabs">
      <!-- 天气查询 -->
      <el-tab-pane label="🌤️ 天气查询" name="weather">
        <div class="weather-container">
          <div class="search-box">
            <el-input 
              v-model="city" 
              placeholder="请输入城市名称 (如: 北京市、上海、杭州)" 
              class="city-input"
              @keyup.enter="getWeather"
              clearable
            >
              <template #prefix>
                <el-icon><Location /></el-icon>
              </template>
            </el-input>
            <el-button 
              type="success" 
              @click="getWeather" 
              :loading="weatherLoading"
              class="search-btn"
            >
              <el-icon><Search /></el-icon>查询天气
            </el-button>
          </div>

          <!-- 天气展示 -->
          <div v-if="weatherData" class="weather-content">
            <!-- 当前天气 -->
            <el-card class="current-weather-card">
              <div class="current-header">
                <div class="city-info">
                  <h3>{{ weatherData.city }}</h3>
                  <p class="update-time">{{ weatherData.reportTime }} 更新</p>
                </div>
                <div class="weather-icon">{{ getWeatherIcon(weatherData.weather) }}</div>
              </div>
              
              <div class="current-details">
                <div class="temperature">
                  <span class="temp-value">{{ weatherData.temperature }}</span>
                  <span class="temp-unit">℃</span>
                </div>
                
                <div class="weather-grid">
                  <div class="weather-item">
                    <div class="item-icon">💨</div>
                    <div class="item-content">
                      <div class="item-label">风力</div>
                      <div class="item-value">{{ weatherData.windDirection }}风 {{ weatherData.windPower }}级</div>
                    </div>
                  </div>
                  <div class="weather-item">
                    <div class="item-icon">💧</div>
                    <div class="item-content">
                      <div class="item-label">湿度</div>
                      <div class="item-value">{{ weatherData.humidity }}%</div>
                    </div>
                  </div>
                  <div class="weather-item">
                    <div class="item-icon">🌡️</div>
                    <div class="item-content">
                      <div class="item-label">体感温度</div>
                      <div class="item-value">{{ weatherData.feelsLike }}℃</div>
                    </div>
                  </div>
                  <div class="weather-item">
                    <div class="item-icon">☁️</div>
                    <div class="item-content">
                      <div class="item-label">天气状况</div>
                      <div class="item-value">{{ weatherData.weather }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 天气预报 -->
            <div v-if="forecastWeather && forecastWeather.length > 0" class="forecast-section">
              <h4 class="section-title">📅 未来4天预报</h4>
              <div class="forecast-grid">
                <el-card 
                  v-for="(day, index) in forecastWeather" 
                  :key="index" 
                  class="forecast-card"
                  shadow="hover"
                >
                  <div class="forecast-date">{{ formatForecastDate(day.date) }}</div>
                  <div class="forecast-main">
                    <div class="forecast-icon">{{ getWeatherIcon(day.dayWeather) }}</div>
                    <div class="forecast-temp">
                      <div class="temp-high">
                        <span class="temp-label">最高</span>
                        <span class="temp-value">{{ day.dayTemp }}°</span>
                      </div>
                      <div class="temp-low">
                        <span class="temp-label">最低</span>
                        <span class="temp-value">{{ day.nightTemp }}°</span>
                      </div>
                    </div>
                  </div>
                  <div class="forecast-desc">{{ day.dayWeather }}</div>
                  <div class="forecast-wind">{{ day.dayWindDirection }}风 {{ day.dayWindPower }}级</div>
                </el-card>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="!weatherLoading" class="empty-state">
            <el-empty description="输入城市名称查看天气信息" :image-size="200" />
          </div>
        </div>
      </el-tab-pane>

      <!-- 地图导航 -->
      <el-tab-pane label="🗺️ 路线导航" name="navigation">
        <div class="navigation-container">
          <div class="navigation-sidebar">
            <!-- 出行方式选择 -->
            <div class="transport-section">
              <h4 class="section-title">出行方式</h4>
              <div class="transport-options">
                <el-button 
                  v-for="mode in transportModes" 
                  :key="mode.value"
                  :style="transportMode === mode.value ? { background: mode.color, borderColor: mode.color, color: 'white' } : {}"
                  :class="['mode-btn', { 'active': transportMode === mode.value }]"
                  @click="changeTransportMode(mode.value)"
                >
                  <div class="mode-icon">{{ mode.icon }}</div>
                  <div class="mode-label">{{ mode.label }}</div>
                </el-button>
              </div>
            </div>

            <!-- 路线规划 -->
            <div class="route-section">
              <h4 class="section-title">路线规划</h4>
              <div class="route-form">
                <div class="input-group">
                  <div class="input-label">
                    <span class="point-circle start"></span>
                    <span>起点</span>
                  </div>
                  <el-input
                    ref="startInputRef"
                    v-model="startPoint"
                    placeholder="请输入起点位置或地址"
                    class="location-input"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Location /></el-icon>
                    </template>
                  </el-input>
                </div>

                <div class="input-group">
                  <div class="input-label">
                    <span class="point-circle end"></span>
                    <span>终点</span>
                  </div>
                  <el-input
                    ref="endInputRef"
                    v-model="endPoint"
                    placeholder="请输入终点位置或地址"
                    class="location-input"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Flag /></el-icon>
                    </template>
                  </el-input>
                </div>

                <el-button 
                  type="success" 
                  class="plan-btn"
                  @click="planRoute"
                  :disabled="!startPoint || !endPoint"
                  :loading="routeLoading"
                >
                  <el-icon><Promotion /></el-icon>
                  开始规划
                </el-button>
              </div>
            </div>

            <!-- 路线详情面板 -->
            <div id="panel" class="route-panel">
              <div v-if="!routeLoading && !routeResults" class="panel-empty">
                <div class="empty-content">
                  <div class="empty-icon">📍</div>
                  <div class="empty-text">输入起点和终点，开始路线规划</div>
                </div>
              </div>
              
              <!-- 路线结果列表 -->
              <div v-if="routeResults" class="route-results">
                <div class="results-header">
                  <h4>路线方案</h4>
                  <el-button type="text" @click="clearResults">清空</el-button>
                </div>
                
                <!-- 路线方案列表 -->
                <div class="routes-list">
                  <div 
                    v-for="(route, index) in routeResults.routes" 
                    :key="index"
                    :class="['route-item', { 'active': selectedRouteIndex === index }]"
                    @click="selectRoute(index)"
                  >
                    <div class="route-header">
                      <div class="route-number">方案 {{ index + 1 }}</div>
                      <div class="route-summary">
                        <span class="route-time">{{ formatTime(route.time) }}</span>
                        <span class="route-distance">{{ formatDistance(route.distance) }}</span>
                        <span v-if="route.taxi_cost" class="route-cost">约¥{{ route.taxi_cost }}</span>
                      </div>
                    </div>
                    
                    <div class="route-details">
                      <!-- 路线步骤 -->
                      <div v-if="route.steps && route.steps.length" class="route-steps">
                        <div 
                          v-for="(step, stepIndex) in route.steps" 
                          :key="stepIndex"
                          class="step-item"
                        >
                          <div class="step-icon">
                            <span v-if="step.action === 'walk'">🚶</span>
                            <span v-else-if="step.action === 'bus'">🚌</span>
                            <span v-else-if="stepIndex === 0">🚗</span>
                            <span v-else-if="stepIndex === route.steps.length - 1">🏁</span>
                            <span v-else>➡️</span>
                          </div>
                          <div class="step-content">
                            <div class="step-text">{{ step.instruction }}</div>
                            <div v-if="step.detail" class="step-detail" style="font-size: 12px; color: #666; margin-top: 2px;">
                              {{ step.detail }}
                            </div>
                            <div class="step-info">
                              <span>{{ formatDistance(step.distance) }}</span>
                              <span>{{ formatTime(step.time) }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 地图容器 -->
          <div id="map-container" class="map-area"></div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { 
  Location, 
  Search, 
  HomeFilled,
  Flag,
  Promotion
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 高德地图配置
const AMAP_KEY = '0783e3e3a68f001b6e7be3cde16e28bd'
const AMAP_SECURITY_CODE = '7e0643f833d109fb8586e047c36150d2'

window._AMapSecurityConfig = {
  securityJsCode: AMAP_SECURITY_CODE,
}

const activeTab = ref('weather')

// 天气相关
const city = ref('')
const weatherLoading = ref(false)
const weatherData = ref(null)
const forecastWeather = ref([])

// 导航相关
const transportMode = ref('driving')
const startPoint = ref('')
const endPoint = ref('')
const routeLoading = ref(false)
const routeResults = ref(null)
const selectedRouteIndex = ref(0)

// 输入框引用
const startInputRef = ref(null)
const endInputRef = ref(null)

// 地图实例
let map = null
let AMap = null
let driving = null
let transfer = null
let riding = null
let walking = null
let currentRoutePlugin = null
let geocoder = null

// 出行方式配置
const transportModes = [
  { value: 'driving', label: '驾车', icon: '🚗', color: '#00B140' },
  { value: 'transit', label: '公共交通', icon: '🚌', color: '#9052CD' },
  { value: 'riding', label: '骑行', icon: '🚴', color: '#0091FF' },
  { value: 'walking', label: '步行', icon: '🚶', color: '#555555' }
]

// 天气图标映射
const weatherIcons = {
  '晴': '☀️',
  '多云': '⛅',
  '阴': '☁️',
  '小雨': '🌦️',
  '中雨': '🌧️',
  '大雨': '💧',
  '暴雨': '🌧️',
  '雷阵雨': '⛈️',
  '雪': '❄️',
  '雾': '🌫️',
  '霾': '😷',
  '沙尘': '🌪️',
  'default': '🌈'
}

onMounted(() => {
  initMap()
  setupInputAutocomplete()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})

// 初始化地图
async function initMap() {
  try {
    AMap = await AMapLoader.load({
      key: AMAP_KEY,
      version: '2.0',
      plugins: [
        'AMap.Driving',
        'AMap.Transfer',
        'AMap.Riding',
        'AMap.Walking',
        'AMap.AutoComplete',
        'AMap.PlaceSearch',
        'AMap.Geocoder',
        'AMap.ToolBar',
        'AMap.Scale',
        'AMap.MapType',
        'AMap.Weather'
      ]
    })

    // 初始化地图
    map = new AMap.Map('map-container', {
      resizeEnable: true,
      zoom: 12,
      center: [116.397428, 39.90923], // 北京中心
      mapStyle: 'amap://styles/fresh' // 使用清新主题
    })

    // 添加地图控件
    map.addControl(new AMap.ToolBar())
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.MapType())

    // 初始化地理编码器
    geocoder = new AMap.Geocoder()

    // 初始化路线规划插件
    // 注意：不再指定 panel: 'panel'，完全由我们自己渲染 UI，避免样式冲突
    driving = new AMap.Driving({
      map: map,
      policy: AMap.DrivingPolicy.LEAST_TIME,
      showTraffic: true,
      hideMarkers: false,
      autoFitView: true
    })

    transfer = new AMap.Transfer({
      map: map,
      policy: AMap.TransferPolicy.LEAST_TIME,
      city: '北京市',
      hideMarkers: false,
      autoFitView: true
    })

    riding = new AMap.Riding({
      map: map,
      hideMarkers: false,
      autoFitView: true
    })

    walking = new AMap.Walking({
      map: map,
      hideMarkers: false,
      autoFitView: true
    })

    currentRoutePlugin = driving

  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败，请刷新页面重试')
  }
}

// 设置输入框自动补全
function setupInputAutocomplete() {
  nextTick(() => {
    if (!AMap || !startInputRef.value || !endInputRef.value) {
      setTimeout(setupInputAutocomplete, 100)
      return
    }

    // 获取原生input元素
    const getInputElement = (ref) => {
      if (!ref) return null
      const el = ref.$el || ref
      return el.querySelector('input')
    }

    const startInput = getInputElement(startInputRef.value)
    const endInput = getInputElement(endInputRef.value)

    if (startInput && endInput) {
      const startAutocomplete = new AMap.AutoComplete({
        input: startInput
      })
      
      const endAutocomplete = new AMap.AutoComplete({
        input: endInput
      })

      // 绑定选择事件
      AMap.event.addListener(startAutocomplete, 'select', (e) => {
        if (e.poi && e.poi.name) {
          startPoint.value = e.poi.name
        }
      })

      AMap.event.addListener(endAutocomplete, 'select', (e) => {
        if (e.poi && e.poi.name) {
          endPoint.value = e.poi.name
        }
      })
    }
  })
}

// 天气相关函数 - 修复版：使用高德天气API
async function getWeather() {
  if (!city.value.trim()) {
    ElMessage.warning('请输入城市名称')
    return
  }

  weatherLoading.value = true
  weatherData.value = null
  forecastWeather.value = []

  try {
    if (!AMap) {
      await initMap()
    }

    // 检查天气插件是否加载
    if (!AMap.Weather) {
       console.warn('AMap.Weather 插件未加载，尝试重新加载')
       // 这里可以尝试重新加载插件，或者提示错误
       // 简单起见，提示错误
       ElMessage.error('天气插件加载失败，请刷新页面')
       weatherLoading.value = false
       return
    }

    // 使用高德天气插件
    const weatherPlugin = new AMap.Weather()

    // 查询实时天气
    weatherPlugin.getLive(city.value, (err, data) => {
      if (!err && data.info === 'OK') {
        weatherData.value = {
          city: data.city,
          weather: data.weather,
          temperature: data.temperature,
          windDirection: data.windDirection,
          windPower: data.windPower,
          humidity: data.humidity,
          reportTime: data.reportTime
        }
        
        // 成功获取实时天气后，查询预报
        weatherPlugin.getForecast(city.value, (err, data) => {
          weatherLoading.value = false
          if (!err && data.info === 'OK' && data.forecasts && data.forecasts.length > 0) {
            forecastWeather.value = data.forecasts.slice(0, 4)
          }
        })
      } else {
        console.error('获取实时天气失败:', err)
        ElMessage.error(err?.info === 'INVALID_USER_KEY' ? 'API Key 无效或配额不足' : '获取天气失败，请检查城市名称')
        weatherLoading.value = false
      }
    })

  } catch (error) {
    console.error('天气查询异常:', error)
    ElMessage.error('天气查询服务繁忙，请稍后重试')
    weatherLoading.value = false
  }
}

// 体感温度计算
function calculateFeelsLike(temp, humidity, windPower) {
  const temperature = parseFloat(temp)
  let feelsLike = temperature
  
  // 简化的体感温度计算
  if (humidity > 70) feelsLike += 2
  if (humidity < 30) feelsLike -= 1
  if (windPower > 4) feelsLike -= 2
  
  return Math.round(feelsLike)
}

// 获取天气图标
function getWeatherIcon(weatherText) {
  for (const [key, icon] of Object.entries(weatherIcons)) {
    if (weatherText.includes(key)) return icon
  }
  return weatherIcons.default
}

// 格式化预报日期
function formatForecastDate(dateStr) {
  const date = new Date(dateStr)
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = weekdays[date.getDay()]
  return `${month}月${day}日 ${weekday}`
}

// 清除所有路线覆盖物
function clearAllRoutes() {
  if (driving) driving.clear()
  if (transfer) transfer.clear()
  if (riding) riding.clear()
  if (walking) walking.clear()
}

// 导航相关函数
function changeTransportMode(mode) {
  transportMode.value = mode
  
  // 清除所有路线覆盖物
  clearAllRoutes()
  
  // 更新当前路线规划插件
  switch (mode) {
    case 'driving':
      currentRoutePlugin = driving
      break
    case 'transit':
      currentRoutePlugin = transfer
      break
    case 'riding':
      currentRoutePlugin = riding
      break
    case 'walking':
      currentRoutePlugin = walking
      break
  }
  
  // 清除之前的UI结果
  clearResults()
  
  // 如果已有起终点，重新规划路线
  if (startPoint.value && endPoint.value) {
    planRoute()
  }
}

// 规划路线
async function planRoute() {
  if (!startPoint.value || !endPoint.value) {
    ElMessage.warning('请输入起点和终点')
    return
  }

  if (!currentRoutePlugin) {
    ElMessage.warning('路线规划服务未初始化')
    return
  }

  routeLoading.value = true
  routeResults.value = null
  selectedRouteIndex.value = 0
  
  // 清除地图上的所有路线
  clearAllRoutes()

  try {
    // 规划路线
    const searchParams = [
      { keyword: startPoint.value, city: '北京市' }, // 默认北京，可优化为当前城市
      { keyword: endPoint.value, city: '北京市' }
    ]

    // 公交需要特殊处理城市
    if (transportMode.value === 'transit') {
       // AMap.Transfer 插件会自动处理跨城，但最好指定城市
       // 这里简单处理，假设在同一城市或由插件自动识别
    }

    currentRoutePlugin.search(
      searchParams,
      (status, result) => {
        routeLoading.value = false
        if (status === 'complete') {
          if (result.info === 'NO_DATA') {
             ElMessage.warning('未找到合适的路线方案')
             return
          }
          processRouteResults(result)
        } else {
          console.error('路线规划失败:', result)
          if (result === 'no_data') {
            ElMessage.warning('该出行方式暂无可行路线，请尝试其他方式')
          } else {
            ElMessage.error('路线规划失败: ' + (result.info || '未知错误'))
          }
        }
      }
    )
  } catch (error) {
    console.error('路线规划异常:', error)
    ElMessage.error('路线规划服务出错')
    routeLoading.value = false
  }
}


// 处理路线结果
function processRouteResults(result) {
  if (!result) return

  let routes = []
  
  // 根据不同的路线插件处理结果
  if (transportMode.value === 'driving' && result.routes) {
    routes = result.routes.map(route => ({
      distance: route.distance,
      time: route.time,
      taxi_cost: route.taxi_cost,
      steps: route.steps || []
    }))
  } else if (transportMode.value === 'transit' && result.plans) {
    routes = result.plans.map(plan => {
      // 解析公交/地铁段
      const steps = []
      if (plan.segments) {
        plan.segments.forEach(seg => {
          // 步行段
          if (seg.transit_mode === 'WALK' && seg.instruction) {
             steps.push({
               instruction: seg.instruction,
               distance: seg.distance,
               time: seg.time,
               action: 'walk'
             })
          } 
          // 公交/地铁段
          else if ((seg.transit_mode === 'BUS' || seg.transit_mode === 'SUBWAY' || seg.transit_mode === 'RAILWAY') && seg.transit) {
             const lineName = seg.transit.lines && seg.transit.lines[0] ? seg.transit.lines[0].name : '未知线路';
             const onStation = seg.transit.on_station ? seg.transit.on_station.name : '未知站点';
             const offStation = seg.transit.off_station ? seg.transit.off_station.name : '未知站点';
             const stopCount = seg.transit.via_num || 0;
             
             steps.push({
               instruction: `乘坐 ${lineName} 从 ${onStation} 到 ${offStation} (${stopCount + 1}站)`,
               distance: seg.distance,
               time: seg.time,
               action: 'bus'
             })
          }
        })
      }
      
      return {
        distance: plan.distance,
        time: plan.time,
        taxi_cost: plan.cost, // 公交可能有票价信息
        steps: steps
      }
    })
  } else if ((transportMode.value === 'riding' || transportMode.value === 'walking') && result.routes) {
    routes = result.routes.map(route => ({
      distance: route.distance,
      time: route.time,
      taxi_cost: null,
      steps: route.steps || []
    }))
  }

  if (routes.length > 0) {
    routeResults.value = {
      mode: transportMode.value,
      routes: routes
    }
    selectedRouteIndex.value = 0
  }
}

// 选择路线方案
function selectRoute(index) {
  selectedRouteIndex.value = index
  // 这里可以添加地图上突出显示选中路线的逻辑
  ElMessage.info(`已选择方案 ${index + 1}`)
}

// 清空结果
function clearResults() {
  routeResults.value = null
  selectedRouteIndex.value = 0
  if (currentRoutePlugin) {
    currentRoutePlugin.clear()
  }
}

// 格式化距离
function formatDistance(meters) {
  if (!meters) return '--'
  if (meters < 1000) return `${Math.round(meters)}米`
  return `${(meters / 1000).toFixed(1)}公里`
}

// 格式化时间
function formatTime(seconds) {
  if (!seconds) return '--'
  if (seconds < 60) return `${seconds}秒`
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}
</script>

<style scoped>
/* 统一绿色主题 */
:root {
  --primary-green: #2e7d32;
  --primary-green-dark: #1b5e20;
  --primary-green-light: #388e3c;
  --secondary-green: #4caf50;
  --accent-green: #66bb6a;
  --light-green: #e8f5e9;
  --lighter-green: #c8e6c9;
}

.tools-page {
  background: linear-gradient(135deg, var(--light-green) 0%, var(--lighter-green) 100%);
  min-height: 100vh;
  border-radius: 20px;
  margin-top: 20px;
  box-shadow: 0 8px 32px rgba(46, 125, 50, 0.1);
}

/* 头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(46, 125, 50, 0.1);
  border: 1px solid var(--lighter-green);
}
.page-title {
  color: #2e7d32;
  font-weight: 700;
}

.page-title {
  margin: 0;
  color: var(--primary-green);
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 标签页样式 */
.custom-tabs {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(46, 125, 50, 0.15);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid var(--lighter-green);
}

:deep(.el-tabs__header) {
  background: rgba(255, 255, 255, 0.5);
  margin: 0;
  border-radius: 16px 16px 0 0;
  border-bottom: 1px solid var(--lighter-green);
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 32px;
  height: 56px;
  line-height: 56px;
  transition: all 0.3s ease;
  color: #666;
}

:deep(.el-tabs__item.is-active) {
  color: var(--primary-green);
  font-weight: 600;
  background: rgba(46, 125, 50, 0.1);
}

:deep(.el-tabs__active-bar) {
  background-color: var(--primary-green);
  height: 3px;
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: #e8f5e9;
}

/* 天气查询样式 */
.weather-container {
  padding: 32px;
  min-height: 500px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  margin: 16px;
  box-shadow: 0 8px 32px rgba(46, 125, 50, 0.1);
  border: 1px solid var(--lighter-green);
}

.search-box {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.city-input {
  width: 400px;
}

:deep(.city-input .el-input__inner) {
  height: 44px;
  border-radius: 22px;
  border: 2px solid var(--lighter-green);
  padding-left: 40px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
}

:deep(.city-input .el-input__inner:focus) {
  border-color: var(--primary-green);
  box-shadow: 0 0 0 3px rgba(46, 125, 50, 0.1);
}

.search-btn {
  height: 44px;
  border-radius: 22px;
  padding: 0 32px;
  font-size: 16px;
}

/* 当前天气卡片 */
.current-weather-card {
  margin-bottom: 32px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  color: white;
  overflow: hidden;
}

.current-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.city-info h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.update-time {
  margin: 4px 0 0;
  font-size: 14px;
  opacity: 0.9;
}

.weather-icon {
  font-size: 48px;
  line-height: 1;
}

.current-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.temperature {
  display: flex;
  align-items: flex-start;
  line-height: 1;
}

.temp-value {
  font-size: 64px;
  font-weight: 700;
}

.temp-unit {
  font-size: 24px;
  margin-top: 8px;
  margin-left: 4px;
  opacity: 0.9;
}

.weather-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.weather-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.item-icon {
  font-size: 24px;
}

.item-content {
  flex: 1;
}

.item-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.item-value {
  font-size: 16px;
  font-weight: 600;
}

/* 天气预报样式 */
.forecast-section {
  margin-top: 32px;
}

.section-title {
  color: #27ae60;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.forecast-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.forecast-card {
  border: none;
  border-radius: 12px;
  background: white;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.forecast-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(39, 174, 96, 0.12);
}

.forecast-date {
  color: #666;
  font-size: 14px;
  text-align: center;
  margin-bottom: 12px;
}

.forecast-main {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-bottom: 12px;
}

.forecast-icon {
  font-size: 32px;
}

.forecast-temp {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.temp-high, .temp-low {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.temp-label {
  font-size: 12px;
  color: #999;
}

.temp-high .temp-value {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: 600;
}

.temp-low .temp-value {
  color: #4dabf7;
  font-size: 16px;
  font-weight: 600;
}

.forecast-desc {
  text-align: center;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.forecast-wind {
  text-align: center;
  font-size: 12px;
  color: #666;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 导航样式 */
.navigation-container {
  display: flex;
  height: 700px;
  gap: 24px;
  padding: 24px;
}

.navigation-sidebar {
  width: 380px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex-shrink: 0;
}

.map-area {
  flex: 1;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 出行方式选择 */
.transport-section {
  margin-bottom: 10px;
}

.transport-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.mode-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: auto;
  padding: 10px 4px;
  border-radius: 10px;
  border: 2px solid #e8f5e9;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.mode-btn:hover {
  border-color: #27ae60;
  background: #f0f9f4;
}

.mode-btn.active {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  color: white;
  border-color: #27ae60;
}

.mode-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.mode-label {
  font-size: 12px;
  font-weight: 500;
}

/* 路线输入 */
.route-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.point-circle {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.point-circle.start {
  background: #2e7d32;
}

.point-circle.end {
  background: #e74c3c;
}

.location-input {
  width: 100%;
}

:deep(.location-input .el-input__inner) {
  height: 44px;
  border-radius: 12px;
  border: 2px solid #e8f5e9;
  padding-left: 40px;
  transition: all 0.3s ease;
}

:deep(.location-input .el-input__inner:focus) {
  border-color: #27ae60;
  box-shadow: 0 0 0 3px rgba(39, 174, 96, 0.1);
}

.plan-btn {
  height: 44px;
  border-radius: 12px;
  margin-top: 8px;
  font-size: 16px;
  background: linear-gradient(135deg, #2e7d32, #388e3c);
  border: none;
}

/* 路线详情面板 */
.route-panel {
  flex: 1;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.panel-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  padding: 40px 20px;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 14px;
  color: #666;
}

/* 路线结果 */
.route-results {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e8f5e9;
  background: #f8fdf9;
}

.results-header h4 {
  margin: 0;
  color: #2e7d32;
  font-size: 16px;
  font-weight: 600;
}

.routes-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.route-item {
  padding: 16px;
  margin-bottom: 8px;
  border: 2px solid #e8f5e9;
  border-radius: 10px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.route-item:hover {
  border-color: #27ae60;
  background: #f0f9f4;
}

.route-item.active {
  border-color: #27ae60;
  background: #e8f5e9;
}

.route-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.route-number {
  font-weight: 600;
  color: #2e7d32;
  font-size: 14px;
}

.route-summary {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

.route-time {
  color: #e74c3c;
  font-weight: 500;
}

.route-distance {
  color: #3498db;
  font-weight: 500;
}

.route-cost {
  color: #f39c12;
  font-weight: 500;
}

/* 路线步骤 */
.route-steps {
  border-left: 2px dashed #e8f5e9;
  margin-left: 6px;
  padding-left: 16px;
}

.step-item {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.step-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.step-icon {
  font-size: 16px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fdf9;
  border-radius: 50%;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-text {
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.4;
}

.step-info {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .navigation-container {
    flex-direction: column;
    height: auto;
  }
  
  .navigation-sidebar {
    width: 100%;
  }
  
  .map-area {
    height: 400px;
  }
  
  .forecast-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .weather-grid {
    grid-template-columns: 1fr;
  }
  
  .forecast-grid {
    grid-template-columns: 1fr;
  }
  
  .transport-options {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .mode-btn {
    height: 60px;
  }
}

/* 覆盖高德地图样式 */
:deep(.amap-logo),
:deep(.amap-copyright) {
  display: none !important;
}

:deep(.amap-info-content) {
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1) !important;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #2e7d32;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #219653;
}

.legend-item.driving .legend-color { background-color: #00B140; }
.legend-item.transit .legend-color { background-color: #9052CD; }
.legend-item.riding .legend-color { background-color: #0091FF; }
.legend-item.walking .legend-color { background-color: #555555; }
</style>
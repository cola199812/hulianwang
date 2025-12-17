import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import ResetPassword from '../views/ResetPassword.vue'
import RouteList from '../views/RouteList.vue'
import CreateRoute from '../views/CreateRoute.vue'
import ActivityList from '../views/ActivityList.vue'
import Routes from '../views/Routes.vue'
import Activity from '../views/Activity.vue'
import Creation from '../views/Creation.vue'
import Gear from '../views/Gear.vue'
import Tools from '../views/Tools.vue'
import Notification from '../views/Notification.vue'
import Profile from '../views/Profile.vue'
import Home from '../views/Home.vue'
import { userInfo } from '../api/user'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', component: Home, meta: { requiresAuth: true } },
  { path: '/login', component: Login, meta: { requiresAuth: false } },
  { path: '/reset-password', component: ResetPassword, meta: { requiresAuth: false } },
  { path: '/routes', component: RouteList, meta: { requiresAuth: true } },
  { path: '/routes/create', component: CreateRoute, meta: { requiresAuth: true } },
  { path: '/activities', component: ActivityList, meta: { requiresAuth: true } },
  { path: '/discover', component: Routes, meta: { requiresAuth: true } },
  { path: '/social', component: Activity, meta: { requiresAuth: true } },
  { path: '/creation', component: Creation, meta: { requiresAuth: true } },
  { path: '/gear', component: Gear, meta: { requiresAuth: true } },
  { path: '/tools', component: Tools, meta: { requiresAuth: true } },
  { path: '/notification', component: Notification, meta: { requiresAuth: true } },
  { path: '/profile', component: Profile, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    try {
      await userInfo()
      next()
    } catch (e) {
      next('/login')
    }
  } else {
    next()
  }
})

export default router


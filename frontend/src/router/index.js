import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '../services/api'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/',
    component: () => import('../views/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/strategies',
        name: 'Strategies',
        component: () => import('../views/Strategies.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/bogle-formula',
        name: 'BogleFormula',
        component: () => import('../views/BogleFormula.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/grid-strategy',
        name: 'GridStrategy',
        component: () => import('../views/GridStrategy.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/company-risk-filter',
        name: 'CompanyRiskFilter',
        component: () => import('../views/CompanyRiskFilter.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/market-entry',
        name: 'MarketEntry',
        component: () => import('../views/MarketEntry.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/cheap-portfolio',
        name: 'CheapPortfolio',
        component: () => import('../views/CheapPortfolio.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/bond-balance',
        name: 'BondBalance',
        component: () => import('../views/BondBalance.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/mine',
        name: 'Mine',
        component: () => import('../views/Mine.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const hasToken = Boolean(getToken())

  if (to.meta.requiresAuth && !hasToken) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.guestOnly && hasToken) {
    return '/dashboard'
  }

  return true
})

export default router
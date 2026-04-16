import {createRouter, createWebHistory} from 'vue-router'

import MainPage from "@/components/MainPage";
import LoginPage from "@/components/LoginPage";

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: LoginPage
    },
    {
        path: '/',
        component: MainPage,
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                name: 'ServerManager',
                components: {
                    default: () => import('@/components/ServerManager'),
                    ServerManager: () => import('@/components/ServerManager'),
                    ConfigManager: () => import('@/components/ConfigManager'),
                    UserManager: () => import('@/components/UserManager')
                }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const user = localStorage.getItem('user')
    if (to.meta.requiresAuth && !user) {
        next('/login')
    } else if (to.path === '/login' && user) {
        // 如果已登录，访问登录页时跳转到首页
        next('/')
    } else {
        next()
    }
})

export default router
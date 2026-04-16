import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from "@/js/router";
const app = createApp(App);
import { ElMessage } from 'element-plus'

// 响应拦截器
axios.interceptors.response.use(
    (response) => {
        if (response.data.code === 0) {
            return response;
        } else {
            ElMessage.error(response.data.message || '请求失败');
            return Promise.reject(new Error(response.data.message || '请求失败'));
        }
    }, 
    (error) => {
        const status = error.response?.status;
        const message = error.response?.data?.message || error.message || '请求异常';
        
        // 处理401未授权
        if (status === 401) {
            ElMessage.error('登录已过期，请重新登录');
            localStorage.removeItem('user');
            router.push('/login');
            return Promise.reject(error);
        }
        
        // 处理403禁止访问
        if (status === 403) {
            ElMessage.error('无权限访问');
            return Promise.reject(error);
        }
        
        // 其他错误
        ElMessage.error(message);
        return Promise.reject(error);
    }
)

app.config.globalProperties.$axios = axios;
app.use(ElementPlus)
    .use(router)

app.mount('#app')

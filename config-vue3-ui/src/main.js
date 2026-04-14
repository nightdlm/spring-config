import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from "@/js/router";
const app = createApp(App);
import { ElMessage } from 'element-plus'

// 使用环境变量配置API基础URL
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:27369';

axios.interceptors.request.use(
    config => {
        config.baseURL = API_BASE_URL;
        return config;
    }
)

axios.interceptors.response.use(
    (response) => {
        if (response.data.code === 0) {
            return response;
        } else {
            console.log(response.data)
            ElMessage.error(response.data.message || '请求失败');
        }
    }, (error) => {
        const message = error.response?.data?.message || error.message || '请求异常';
        ElMessage.error(message);
        return Promise.reject(error);
    }
)

app.config.globalProperties.$axios = axios;
app.use(ElementPlus)
    .use(router)

app.mount('#app')

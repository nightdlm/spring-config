import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from "@/js/router";
const app = createApp(App);
import { ElMessage } from 'element-plus'
axios.interceptors.request.use(
    config => {
        config.baseURL = 'http://localhost:27369';
        return config;
    }
)

axios.interceptors.response.use(
    (response,error) => {
        if (error) {
            ElMessage.error("请求异常");
        } else {
            if (response.data.code === 0) {
                return response;
            } else {
                ElMessage.error(response.data.message);
            }
        }
    }
)
app.config.globalProperties.$axios = axios;
app.use(ElementPlus)
    .use(router)

app.mount('#app')

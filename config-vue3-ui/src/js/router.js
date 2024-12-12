import {createRouter, createWebHistory} from 'vue-router'

import ServerManager from "@/components/ServerManager";
import ConfigManager from "@/components/ConfigManager";

const routes = [
    {
        path: '/',
        components: {
            default: ServerManager,
            ServerManager,
            ConfigManager
        },
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
import { createApp } from 'vue'
import Vant from 'vant'
import App from './App.vue'
import router from './router'
import 'vant/lib/index.css'
import './style.css'

const app = createApp(App)
app.use(router)
app.use(Vant)
app.mount('#app')

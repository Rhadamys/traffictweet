import Vue from 'vue';
import VueRouter from 'vue-router';
import VueResource from 'vue-resource';
import App from './vue/app.vue';
import Index from './vue/index.vue';
import Panel from './vue/panel.vue';
import Graphs from './vue/graphs.vue';
import About from './vue/about.vue';
import Occurrence from './vue/occurrence.vue';
require("./css/app.scss");

Vue.use(VueRouter);
Vue.use(VueResource);

const routes = [
    {
        path: '/index',
        alias: '/',
        component: Index
    },
    {
        path: '/panel-tweets',
        component: Panel
    },
    {
        path: '/graficos',
        component: Graphs
    },
    {
        path: '/nosotros',
        component: About
    },
    {
        path: '/occurrence/:id',
        component: Occurrence,
        props: true,
    }
]

// Create the router instance and pass the `routes` option
const router = new VueRouter({
    routes
})


new Vue({
    el: '#app',
    router,
    render: h => h(App)
})

<template>
    <div>
        <sidebar></sidebar>
        <transition name="slide-fade" mode="out-in">
            <router-view class="app-container"></router-view>
        </transition>
    </div>
</template>
<script>
import Sidebar from './sidebar.vue';

export default {
    components: {
        'sidebar': Sidebar
    }
}
</script>
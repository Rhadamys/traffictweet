<template>
    <nav class="sidebar">
        <a v-for="item in items" v-bind:href="item.link" class="btn text-center" v-bind:class="{ active: isActive(item.link) }"
            v-on:click="itemActive = item.link">
            <i class="fa" v-bind:class="item.icon" aria-hidden="true"></i>
            <br>
            <div class="text">{{ item.text }}</div>
        </a>
    </nav>
</template>
<script>
export default {
    data: function() {
        return {
            items: [
                {
                    link: '#/',
                    text: 'Inicio',
                    icon: {
                        'fa-home': true
                    }
                },
                {
                    link: '#/panel-tweets',
                    text: 'Panel',
                    icon: {
                        'fa-th': true
                    }
                },
                {
                    link: '#/graficos',
                    text: 'Gráficos',
                    icon: {
                        'fa-bar-chart': true
                    }
                },
                {
                    link: '#/nosotros',
                    text: 'Nosotros',
                    icon: {
                        'fa-users': true
                    }
                }
            ],
            itemActive: ''
        }
    },
    methods: {
        isActive: function(link) {
            return ('#' + this.$route.path) === link || this.itemActive === link
        }
    }
}
</script>
<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li class="active">Panel de tweets</li>
        </ol>
        <div v-for="tweet in tweets" class="col-md-4">
            <tweet v-bind="tweet"></tweet>
        </div>
    </div>
</template>
<script>
import Tweet from './tweet.vue';

export default {
    data: function () {
        return {
            tweets: []
        }
    },
    components: {
        'tweet': Tweet
    },
    mounted: function() {
        this.$http.get('http://localhost:9090/occurrences/')
            .then(response=>{
                this.tweets = response.body;
            }, response=>{
                console.log('Error cargando lista');
            })
    }
}
</script>
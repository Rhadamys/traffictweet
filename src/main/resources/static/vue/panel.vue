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
            tweets: [
                {
                    user: 'Usuario 1',
                    text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce non vehicula ipsum. Sed odio orci, mollis vel tristique et, consectetur...',
                    category: 'Accidente',
                    image: '../img/user.png',
                    url: 'http://www.google.cl'
                }, {
                    user: 'Usuario 2',
                    text: 'Etiam id convallis orci, sed pretium magna. Nulla sed leo lorem. Donec in euismod est. Suspendisse ut sapien at dui tincidunt vulputate...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.facebook.com'
                }, {
                    user: 'Usuario 3',
                    text: 'Duis gravida lectus ac faucibus vehicula. Sed nec neque interdum, fringilla diam venenatis, luctus urna. Vestibulum ante ipsum primis in...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.youtube.cl'
                }, {
                    user: 'Usuario 2',
                    text: 'Etiam id convallis orci, sed pretium magna. Nulla sed leo lorem. Donec in euismod est. Suspendisse ut sapien at dui tincidunt vulputate...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.facebook.com'
                }, {
                    user: 'Usuario 1',
                    text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce non vehicula ipsum. Sed odio orci, mollis vel tristique et, consectetur...',
                    category: 'Accidente',
                    image: '../img/user.png',
                    url: 'http://www.google.cl'
                }, {
                    user: 'Usuario 3',
                    text: 'Duis gravida lectus ac faucibus vehicula. Sed nec neque interdum, fringilla diam venenatis, luctus urna. Vestibulum ante ipsum primis in...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.youtube.cl'
                }, {
                    user: 'Usuario 2',
                    text: 'Etiam id convallis orci, sed pretium magna. Nulla sed leo lorem. Donec in euismod est. Suspendisse ut sapien at dui tincidunt vulputate...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.facebook.com'
                }, {
                    user: 'Usuario 1',
                    text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce non vehicula ipsum. Sed odio orci, mollis vel tristique et, consectetur...',
                    category: 'Accidente',
                    image: '../img/user.png',
                    url: 'http://www.google.cl'
                }, {
                    user: 'Usuario 3',
                    text: 'Duis gravida lectus ac faucibus vehicula. Sed nec neque interdum, fringilla diam venenatis, luctus urna. Vestibulum ante ipsum primis in...',
                    category: 'Congestión',
                    image: '../img/user.png',
                    url: 'http://www.youtube.cl'
                }
            ]
        }
    },
    components: {
        'tweet': Tweet
    }
}
</script>
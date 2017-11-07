<template>
    <div class="panel">
        <div class="panel-heading">
            <div class="tweet-heading">
                <img v-bind:src="image"
                     class="img-circle img-responsive img"
                     alt="Imagen de usuario"/>
                @{{ username }}
            </div>
            <i class="tweet-date">
                {{ commune.name }}&ensp;
                <i class="fa fa-map-marker" aria-hidden="true"></i>
            </i>
        </div>
        <div class="panel-body">
            <span v-for="category in categories" class="badge" v-bind:class="category.key">{{ category.name }}</span>
        </div>
        <div class="panel-body">
            <!--<div v-html="parsedText"></div>-->
            {{ text }}
        </div>
        <div v-if="details" class="panel-footer tweet-footer">
            <i class="tweet-date">{{ date }}</i>
            <a v-bind:href="'#/occurrences/' + tweetId">Ver detalles...</a>
        </div>
    </div>
</template>
<script>
export default {
    props: ["tweetId", "image", "username", "text", "commune", "categories", "details", "date"],
    /*data: function() {
        const urlRegex = /(http|https|ftp|ftps)\:\/\/[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(\/\S*)?/g;
        const hashTagRegex = /\B(\#[\wáéíóúñÁÉÍÓÚÑ]+\b)/g;
        const usernameRegex = /\B(\@\w+\b)/g;
        const parsedText = this.text
            .replace(urlRegex, function(url) {
                return '<a href="' + url + '" target="_blank">' + url + '</a>';
            })
            .replace(hashTagRegex, function(hashTag) {
                return '<a href="https://twitter.com/hashtag/' + hashTag.substr(1) + '" target="_blank">' + hashTag + '</a>';
            })
            .replace(usernameRegex, function(username) {
                return '<a href="https://twitter.com/' + username.substr(1) + '" target="_blank">' + username + '</a>';
            });

        return {
            parsedText: parsedText
        }
    }*/
}
</script>
<template>
    <div class="panel">
        <div class="panel-heading">
            <h4>
                {{ username }}
                <img v-bind:src="image"
                     class="pull-left img-circle img-responsive"
                     style="padding: 0px; margin-right: 1em; height: 1.2em;"
                     alt="Imagen de usugradle ario"/>
            </h4>
        </div>
        <div class="panel-body">
            Categoría: {{ category }}
        </div>
        <div class="panel-body">
            {{ content }} <a v-bind:href="url" target="_blank">{{ url }}</a>
        </div>
        <div class="panel-footer">
            <a class="btn-block text-right" target="_blank">Ver detalles...</a>
        </div>
    </div>
</template>
<script>
export default {
    props: ["image", "username", "category", "content", "url"]
}
</script>
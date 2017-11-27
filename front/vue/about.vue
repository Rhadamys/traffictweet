<template>
    <div class="container">
        <div class="col-md-12">
            <div class="panel panel-default">
                <div class="panel-heading text-center">
                    <h4><b>Nosotros</b></h4>
                </div>
            </div>
        </div>
        <div class="col-md-4" v-for="(member, index) in members" v-bind:class="{ 'col-md-offset-2': index == 3 }">
            <div class="thumbnail">
                <div class="caption text-center">
                    <h2><b>{{ member.name }}</b></h2>
                    <h3>{{ member.role }}</h3>
                    Universidad de Santiago de Chile<br>
                    Departamento de Ingeniería Informática<br>
                    <a v-bind:href="'mailto:' + member.mail">{{ member.mail }}</a>
                </div>
                <div class="footer"></div>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    data: function() {
        return {
            members: [
                {
                    name: "Luis Migryk",
                    role: "Scrum master",
                    mail: "luis.migryk@usach.cl"
                },
                {
                    name: "Mario Álvarez",
                    role: "Fullstack developer",
                    mail: "mario.alvarez.m@usach.cl"
                },
                {
                    name: "Cristian Sepúlveda",
                    role: "Encargado área Back-end",
                    mail: "cristian.sepulveda.c@usach.cl"
                },
                {
                    name: "Nicolás Paredes",
                    role: "Back-end",
                    mail: "nicolas.paredesa@usach.cl"
                },
                {
                    name: "Fabián Escobar",
                    role: "Back-end",
                    mail: "fabian.escobar@usach.cl"
                }
            ]
        }
    }
}
</script>
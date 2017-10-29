<template>
    <div class="container">
        <ol class="breadcrumb">
            <li><a href="#/">Inicio</a></li>
            <li class="active">Gráficos</li>
        </ol>
        <div class="col-md-8">
            <canvas id="chart-semestre" class="panel panel-default"></canvas>
        </div>
        <div class="col-md-4">
            <canvas id="chart-resumen" class="panel panel-default"></canvas>
        </div>
    </div>
</template>
<script>
export default {
    mounted: function () {
        var style = getComputedStyle(document.body);

        var ctxSem = document.getElementById("chart-semestre").getContext('2d');
        new Chart(ctxSem, {
            type: 'line',
            data: {
                labels: ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio"],
                datasets: [{
                    label: "Puntos de congestión",
                    backgroundColor: style.getPropertyValue('--graph-background-1'),
                    borderColor: style.getPropertyValue('--graph-border-1'),
                    fill: false,
                    data: [10, 5, 16, 21, 19, 12 ]
                }, {
                    label: "Accidentes",
                    backgroundColor: style.getPropertyValue('--graph-background-2'),
                    borderColor: style.getPropertyValue('--graph-border-2'),
                    fill: false,
                    data: [4, 9, 12, 7, 5, 3 ]
                }, {
                    label: "Otros",
                    backgroundColor: style.getPropertyValue('--graph-background-3'),
                    borderColor: style.getPropertyValue('--graph-border-3'),
                    fill: false,
                    data: [12, 13, 9, 10, 15, 21 ]
                }]
            },
            options: {
                responsive: true,
                title:{
                    display: true,
                    text: 'Eventos de tránsito en los últimos 6 meses'
                },
                scales: {
                    xAxes: [{
                        display: true,
                    }],
                    yAxes: [{
                        display: true,
                    }]
                }
            }
        });

        var ctxRes = document.getElementById("chart-resumen").getContext('2d');
        new Chart(ctxRes, {
            type: 'bar',
            data: {
                labels: ["Accidentes", "Congestión", "Otros"],
                datasets: [{
                    label: "# de eventos",
                    data: [10, 5, 21],
                    backgroundColor:
                    [
                        style.getPropertyValue('--graph-background-1'),
                        style.getPropertyValue('--graph-background-2'),
                        style.getPropertyValue('--graph-background-3')
                    ],
                    borderColor:
                    [
                        style.getPropertyValue('--graph-border-1'),
                        style.getPropertyValue('--graph-border-2'),
                        style.getPropertyValue('--graph-border-3')
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                title:{
                    display: true,
                    text: 'Eventos de tránsito en los últimos 6 meses'
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
    }
}
</script>
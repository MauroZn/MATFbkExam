import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
})
export class HomepageComponent implements OnInit {
  chart: any;
  isLoading = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchDataAndCreateChart();
  }

  refreshData() {
    this.isLoading = true;
    this.fetchDataAndCreateChart();
  }

  fetchDataAndCreateChart() {
    this.http.get('http://localhost:8080/api/logs').subscribe({
      next: (data: any) => {
        this.createChart(data);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
        this.isLoading = false;
      },
    });
  }

  createChart(data: any) {
    // Destroy existing chart if it exists
    if (this.chart) {
      this.chart.destroy();
    }

    // Process data for the histogram
    const timestamps = data.map((log: any) =>
      new Date(log.timestamp).getHours()
    );
    const hourCounts = new Array(24).fill(0);
    timestamps.forEach((hour: number) => {
      hourCounts[hour]++;
    });

    // Get the canvas element
    const canvas = document.getElementById('logChart') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');

    if (ctx) {
      this.chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
          datasets: [
            {
              label: 'Log Entries per Hour',
              data: hourCounts,
              backgroundColor: 'rgba(76, 175, 80, 0.5)',
              borderColor: 'rgba(76, 175, 80, 1)',
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1,
              },
            },
          },
        },
      });
    }
  }
}

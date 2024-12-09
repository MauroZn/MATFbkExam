import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
})
export class HomepageComponent implements OnInit {
  charts: any[] = [];
  isLoading = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchDataAndCreateCharts();
  }

  refreshData() {
    this.isLoading = true;
    this.fetchDataAndCreateCharts();
  }

  fetchDataAndCreateCharts() {
    this.http.get('http://localhost:8080/api/logs').subscribe({
      next: (data: any) => {
        this.createCharts(data);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
        this.isLoading = false;
      },
    });
  }

  createCharts(data: any) {
    // Destroy existing charts if they exist
    this.charts.forEach((chart) => chart.destroy());
    this.charts = [];

    // Process data for the histograms
    const timestamps = data.map((log: any) =>
      new Date(log.timestamp).getHours()
    );
    const hourCounts = new Array(24).fill(0);
    timestamps.forEach((hour: number) => {
      hourCounts[hour]++;
    });

    // Create first chart
    const ctx1 = document.getElementById('logChart1') as HTMLCanvasElement;
    const chart1 = new Chart(ctx1, {
      type: 'bar',
      data: {
        labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
        datasets: [
          {
            label: 'Logs per Hour',
            data: hourCounts,
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
          },
        ],
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    });
    this.charts.push(chart1);

    // Create additional charts as needed
    // Example: Create a second chart
    const ctx2 = document.getElementById('logChart2') as HTMLCanvasElement;
    const chart2 = new Chart(ctx2, {
      type: 'line',
      data: {
        labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
        datasets: [
          {
            label: 'Logs per Hour (Line)',
            data: hourCounts,
            backgroundColor: 'rgba(153, 102, 255, 0.2)',
            borderColor: 'rgba(153, 102, 255, 1)',
            borderWidth: 1,
          },
        ],
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    });
    this.charts.push(chart2);
  }
}

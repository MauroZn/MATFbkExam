import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, LineElement, PointElement, ArcElement, BarController, LineController } from 'chart.js'; // <-- Add these imports

Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, LineElement, PointElement, ArcElement, BarController, LineController); // <-- Register them


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
    this.http.get('http://localhost:9000/api/logs').subscribe({
      next: (data: any) => {
        console.log('Fetched data:', data);
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

    // Process data for the histograms
    const searchedTimestamps = data
      .filter((log: any) => log.requestType === 'Searched')
      .map((log: any) => new Date(log.timestamp).getHours());
    const soldTimestamps = data
      .filter((log: any) => log.requestType === 'Sold')
      .map((log: any) => new Date(log.timestamp).getHours());

    const searchedHourCounts = new Array(24).fill(0);
    const soldHourCounts = new Array(24).fill(0);

    searchedTimestamps.forEach((hour: number) => {
      searchedHourCounts[hour]++;
    });
    soldTimestamps.forEach((hour: number) => {
      soldHourCounts[hour]++;
    });

    // Create first chart
    const ctx1 = document.getElementById('logChart1') as HTMLCanvasElement;
    const chart1 = new Chart(ctx1, {
      type: 'bar',
      data: {
        labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
        datasets: [
          {
            label: 'Api Calls',
            data: searchedHourCounts,
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
      plugins: [
        {
          id: 'backgroundColor', // A unique identifier for your custom plugin
          beforeDraw: (chart) => {
            const { ctx, width, height } = chart;
            ctx.save();
            ctx.fillStyle = 'rgba(50, 50, 50, 1)'; // Set the background color
            ctx.fillRect(0, 0, width, height); // Fill the entire canvas
            ctx.restore();
          },
        },
      ],
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
            label: 'Searched Items',
            data: searchedHourCounts,
            backgroundColor: 'rgba(0, 255, 230, 0.2)',
            borderColor: 'rgba(0, 255, 230, 1)',
            borderWidth: 1,
          },
          {
            label: 'Sold Items',
            data: soldHourCounts,
            backgroundColor: 'rgba(222, 0, 255, 0.2)',
            borderColor: 'rgba(222, 0, 255, 1)',
            borderWidth: 1,
          }
        ],
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
      plugins: [
        {
          id: 'backgroundColor', // A unique identifier for your custom plugin
          beforeDraw: (chart) => {
            const { ctx, width, height } = chart;
            ctx.save();
            ctx.fillStyle = 'rgba(50, 50, 50, 1)'; // Set the background color
            ctx.fillRect(0, 0, width, height); // Fill the entire canvas
            ctx.restore();
          },
        },
      ],
    });
    this.charts.push(chart2);
  }
}

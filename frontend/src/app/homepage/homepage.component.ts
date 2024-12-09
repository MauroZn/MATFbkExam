import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, LineElement, PointElement, ArcElement, BarController, LineController, ChartType, Plugin  } from 'chart.js'; // <-- Add these imports

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

  //Fetch Data and create charts
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

  //Create charts for each chart
  createCharts(data: any) {
    // Destroy existing charts if they exist
    this.charts.forEach((chart) => chart.destroy());
    this.charts = [];



    //CHART1
    // Process data for the histograms
    const backgroundColorPlugin: Plugin<ChartType> = {
      id: 'backgroundColor',
      beforeDraw: (chart: Chart) => {
        const { ctx, width, height } = chart;
        if (ctx && width && height) {
          ctx.save();
          ctx.fillStyle = 'rgba(50, 50, 50, 1)'; // Set the background color
          ctx.fillRect(0, 0, width, height); // Fill the entire canvas
          ctx.restore();
        }
      },
    };

    // Set start and end date for the last week
    const today = new Date();
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(today.getDate() - 5);

// Initialize arrays for last 7 days
    const daysInWeek = new Array(7).fill(0);
    const labels: string[] = [];

// Generate labels for the last 7 days
    for (let i = 0; i < 7; i++) {
      const date = new Date(oneWeekAgo);
      date.setDate(oneWeekAgo.getDate() + i);
      labels.push(date.toISOString().split('T')[0]); // Format: YYYY-MM-DD
    }

// Debug labels
    console.log('Generated Labels:', labels);

// Count API calls for each day in the last week
    data.forEach((log: any) => {
      const logDate = new Date(log.timestamp).toISOString().split('T')[0]; // Ensure format matches labels
      console.log('Log Date:', logDate); // Debug log dates
      const dayIndex = labels.indexOf(logDate);
      if (dayIndex !== -1) {
        daysInWeek[dayIndex]++;
      }
    });

// Debug the final counts
    console.log('API Calls Per Day:', daysInWeek);

// Create line chart
    const ctx1 = document.getElementById('logChart1') as HTMLCanvasElement;
    const chart1 = new Chart(ctx1, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'API Calls Per Day (Last Week)',
            data: daysInWeek,
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
            fill: true,
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          x: {
            title: {
              display: true,
              text: 'Date',
            },
          },
          y: {
            title: {
              display: true,
              text: 'Number of API Calls',
            },
            beginAtZero: true,
          },
        },
      },
      plugins: [backgroundColorPlugin],
    });


    this.charts.push(chart1);





    //CHART2
    // Process data for the chart2
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

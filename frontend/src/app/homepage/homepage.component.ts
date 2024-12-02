import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

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
    // Implementation to create the chart using the fetched data
  }
}

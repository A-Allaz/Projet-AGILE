package com.example.pld_agile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import Model.CityMap;

@SpringBootApplication
public class PLDAgileApplication {
	public static void main(String[] args) {
		SpringApplication.run(PLDAgileApplication.class, args);


		CityMap cityMap = new CityMap();
		cityMap.loadMap("src/main/java/com/example/pld_agile/Map/petitPlan.xml");

		System.out.println("Intersections: " + cityMap.getIntersections().size());
		System.out.println("Road Segments: " + cityMap.getRoadSegments().size());
	}
}
package com.example.pld_agile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import Model.CityMap;
import Model.Intersection;
import Model.RoadSegment;

@SpringBootApplication
public class PLDAgileApplication {
	public static void main(String[] args) {
		SpringApplication.run(PLDAgileApplication.class, args);


		CityMap cityMap = new CityMap();
		cityMap.loadMap("src/main/java/com/example/pld_agile/Map/petitPlan.xml");

		System.out.println("Intersections: " + cityMap.getIntersections().size());
		for (Intersection intersection : cityMap.getIntersections()) {
			System.out.println(intersection);
		}

		System.out.println("Road Segments: " + cityMap.getRoadSegments().size());
		for (RoadSegment roadSegment : cityMap.getRoadSegments()) {
			System.out.println(roadSegment);
		}
	}
}
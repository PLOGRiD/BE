package com.example.plogrid.domain.trash.entity;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import com.example.plogrid.domain.common.BaseEntity;
import com.example.plogrid.domain.plogging.entity.Plogging;
import com.example.plogrid.domain.trash.entity.enums.TrashCategory;
import com.example.plogrid.domain.trash.entity.enums.TrashSubCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Trash extends BaseEntity {

	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plogging_id", nullable = false)
	private Plogging plogging;

	@Column(nullable = false, unique = true)
	private String sourceRecordId;

	@Column(nullable = false)
	private String trashImage;

	@Column(nullable = false, columnDefinition = "geography(Point,4326)")
	private Point location;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TrashCategory category;

	@Enumerated(EnumType.STRING)
	private TrashSubCategory subCategory;

	public double getLatitude() {
		return location.getY();
	}

	public double getLongitude() {
		return location.getX();
	}

	public static Trash create(Plogging plogging, String sourceRecordId, String trashImage, double latitude,
		double longitude, TrashCategory category, TrashSubCategory subCategory) {
		return Trash.builder()
			.plogging(plogging)
			.sourceRecordId(sourceRecordId)
			.trashImage(trashImage)
			.location(toPoint(latitude, longitude))
			.category(category)
			.subCategory(subCategory)
			.build();
	}

	private static Point toPoint(double latitude, double longitude) {
		return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
	}
}

-- Enable PostGIS Extension (Required for spatial query support)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Spatial GIST indexes for fast proximity searches.
-- Note: These indexes will be applied once the tables are created by Hibernate.
-- If the tables don't exist yet, they will run fine or Hibernate will generate them and these index queries can be run subsequently.
CREATE INDEX IF NOT EXISTS idx_reports_geom ON reports USING gist(geom);
CREATE INDEX IF NOT EXISTS idx_crime_data_geom ON crime_data USING gist(geom);
CREATE INDEX IF NOT EXISTS idx_cctv_data_geom ON cctv_data USING gist(geom);
CREATE INDEX IF NOT EXISTS idx_lighting_data_geom ON lighting_data USING gist(geom);

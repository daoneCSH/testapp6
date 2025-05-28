SELECT COUNT(*)
FROM pg_catalog.pg_tables
WHERE schemaname = 'public'
  AND tablename = ?

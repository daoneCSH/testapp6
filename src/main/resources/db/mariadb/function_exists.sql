SELECT COUNT(*)
FROM information_schema.routines
WHERE routine_schema = DATABASE()
  AND routine_type = 'FUNCTION'
  AND routine_name = ?

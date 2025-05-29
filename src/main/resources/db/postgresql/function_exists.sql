SELECT COUNT(*)
FROM pg_proc p
     JOIN pg_namespace n ON p.pronamespace = n.oid
WHERE p.proname = ?
  AND n.nspname = 'public';
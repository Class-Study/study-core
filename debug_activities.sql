-- Check if activities exist for the student
SELECT f.id as folder_id, f.name as folder_name, f.position,
       COUNT(a.id) as total_activities
FROM study.folders f
LEFT JOIN study.activities a ON a.folder_id = f.id
WHERE f.student_id = '36862c65-20a9-4af4-8198-0178fb8650a9'
GROUP BY f.id, f.name, f.position
ORDER BY f.position;

-- List all activities for this student
SELECT a.id, a.title, a.type, a.folder_id, a.created_at
FROM study.activities a
JOIN study.folders f ON f.id = a.folder_id
WHERE f.student_id = '36862c65-20a9-4af4-8198-0178fb8650a9'
ORDER BY f.position, a.created_at;


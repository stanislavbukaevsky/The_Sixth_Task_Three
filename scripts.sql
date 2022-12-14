select *
from student;

select *
from faculty;
-- 1
select *
from student
where age < 30
  and age > 20;
-- 2
select name
from student;
-- 3
select *
from student
where name like '%о%';
-- 4
select *
from student
where age < student.id;
-- 5
select *
from student
order by age;
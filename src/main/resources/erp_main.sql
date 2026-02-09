CREATE DATABASE IF NOT EXISTS erp_main;
USE erp_main;

-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: erp_main
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `title` varchar(100) NOT NULL,
  `credits` int NOT NULL,
  PRIMARY KEY (`course_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'CSE101','IP',5),(2,'MAT101','ppppp',4),(3,'PHY101','Engineering Physics',4),(4,'ENG101','English Communication',3),(5,'EEE101','Basic Electrical Engineering',4),(6,'CSE102','Problem Solving with C',3),(7,'MAT102','LInear ALGEBRA',4),(8,'ME101','Engineering Graphics',3),(9,'CSE103','Computer Essentials',2),(10,'EVS101','Environmental Science',2),(11,'CSE201','Data Structures',4),(12,'MAT201','Calculus II',4),(13,'ECE201','Digital Logic Design',4),(14,'CSE202','Object Oriented Programming',4),(15,'PHY201','Physics Lab',2),(16,'HUM201','Professional Ethics',2),(17,'MAT202','Discrete Mathematics',4),(18,'EEE201','Circuits and Networks',3),(19,'CSE203','Unix and Shell Programming',3),(20,'ENG201','Technical Writing',2),(21,'CSE301','Algorithms',4),(22,'CSE302','Database Management Systems',4),(23,'CSE303','Computer Organization',4),(24,'MAT301','Probability and Statistics',4),(25,'CSE304','Operating Systems',4),(26,'ECE301','Microprocessors',3),(27,'CSE305','Data Structures Lab',2),(28,'CSE306','Operating Systems Lab',2),(29,'CSE307','DBMS Lab',2),(30,'CSE308','Software Engineering',3),(31,'CSE401','Design and Analysis of Algorithms',4),(32,'CSE402','Computer Networks',4),(33,'CSE403','Theory of Computation',4),(34,'CSE404','Embedded Systems',3),(35,'CSE405','Signals and Systems',4),(36,'CSE406','Network Security',3),(37,'MAT401','Numerical Methods',3),(38,'CSE407','Computer Networks Lab',2),(39,'CSE408','Compiler Design Basics',3),(40,'CSE409','Logic and Computation',3),(41,'CSE501','Machine Learning',4),(42,'CSE502','Artificial Intelligence',4),(43,'CSE503','Compiler Design',4),(44,'CSE504','Distributed Systems',4),(45,'CSE505','Software Architecture',3),(46,'CSE506','Cloud Computing',4),(47,'CSE507','Big Data Analytics',4),(48,'CSE508','Machine Learning Lab',2),(49,'CSE509','Cloud Computing Lab',2),(50,'CSE510','Data Mining',3),(51,'CSE601','Deep Learning',4),(52,'CSE602','Internet of Things',4),(53,'CSE603','Computer Vision',4),(54,'CSE604','Blockchain Technologies',3),(55,'CSE605','Mobile Application Development',3),(56,'CSE606','Cyber Security',4),(57,'CSE607','Advanced Database Systems',4),(58,'CSE608','IoT Lab',2),(59,'CSE609','Security Lab',2),(60,'CSE610','Optimization Techniques',3),(61,'CSE701','Natural Language Processing',4),(62,'CSE702','Robotics',4),(63,'CSE703','Game Development',3),(64,'CSE704','Distributed Machine Learning',4),(65,'CSE705','Augmented Reality',4),(66,'CSE706','High Performance Computing',4),(67,'CSE707','DevOps Engineering',3),(68,'CSE708','Industrial IoT',3),(69,'CSE709','Cyber Forensics',3),(70,'CSE710','Software Testing and QA',3),(71,'CSE801','Research Methodology',3),(72,'CSE802','AI Ethics',3),(73,'CSE803','Advanced Cybersecurity',4),(74,'CSE804','Parallel Computing',4),(75,'CSE805','Advanced Machine Learning',4),(76,'CSE806','Data Science Project',6),(77,'CSE807','Industrial Training',6),(78,'CSE808','Cloud Architecture',3),(79,'CSE809','Network Automation',3),(80,'CSE810','Capstone Project',8),(81,'ECE6969','ELD',4),(82,'CSE2','sssss',3),(85,'CSE134','IP2',3),(86,'Mritunjay','303',2),(87,'S303','ddddd',14),(88,'Adccc','SSSS',1),(89,'Mritun','opppp',1);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollments` (
  `enrollment_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `section_id` int NOT NULL,
  `status` enum('enrolled','dropped','completed') DEFAULT 'enrolled',
  PRIMARY KEY (`enrollment_id`),
  UNIQUE KEY `student_id` (`student_id`,`section_id`),
  UNIQUE KEY `unique_student_section` (`student_id`,`section_id`),
  KEY `fk_enrollment_section` (`section_id`),
  CONSTRAINT `fk_enrollment_section` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`),
  CONSTRAINT `fk_enrollment_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollments`
--

LOCK TABLES `enrollments` WRITE;
/*!40000 ALTER TABLE `enrollments` DISABLE KEYS */;
INSERT INTO `enrollments` VALUES (64,11,83,'enrolled'),(65,11,82,'enrolled'),(66,11,78,'enrolled'),(67,11,79,'enrolled'),(68,11,80,'enrolled'),(69,11,81,'enrolled'),(70,12,78,'enrolled'),(71,12,79,'enrolled'),(72,12,80,'enrolled'),(73,12,81,'enrolled'),(74,12,82,'enrolled'),(75,12,83,'enrolled');
/*!40000 ALTER TABLE `enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade_slabs`
--

DROP TABLE IF EXISTS `grade_slabs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_slabs` (
  `slab_id` int NOT NULL AUTO_INCREMENT,
  `instructor_id` int NOT NULL,
  `section_id` int NOT NULL,
  `grade` varchar(5) NOT NULL,
  `min_marks` int NOT NULL,
  `max_marks` int NOT NULL,
  `grade_point` double NOT NULL,
  PRIMARY KEY (`slab_id`),
  UNIQUE KEY `unique_slab` (`section_id`,`grade`),
  KEY `instructor_id` (`instructor_id`),
  CONSTRAINT `grade_slabs_ibfk_1` FOREIGN KEY (`instructor_id`) REFERENCES `instructors` (`instructor_id`),
  CONSTRAINT `grade_slabs_ibfk_2` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade_slabs`
--

LOCK TABLES `grade_slabs` WRITE;
/*!40000 ALTER TABLE `grade_slabs` DISABLE KEYS */;
INSERT INTO `grade_slabs` VALUES (10,33,83,'A',90,100,10),(11,33,83,'B',80,89,9),(12,33,83,'C',0,79,2),(13,33,78,'A',50,100,10),(14,33,78,'C',1,49,7),(15,33,79,'A',70,100,10),(16,33,79,'D',0,69,4),(17,33,80,'A',60,100,10),(18,33,80,'C',0,59,5),(19,33,81,'A',30,100,10),(20,33,81,'C',0,29,5),(21,33,82,'A',0,100,10);
/*!40000 ALTER TABLE `grade_slabs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grades` (
  `enrollment_id` int NOT NULL,
  `component` varchar(50) NOT NULL,
  `score` decimal(5,2) DEFAULT '0.00',
  `weight` int DEFAULT NULL,
  `final_grade` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`enrollment_id`,`component`),
  CONSTRAINT `fk_grade_enrollment` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`enrollment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grades`
--

LOCK TABLES `grades` WRITE;
/*!40000 ALTER TABLE `grades` DISABLE KEYS */;
INSERT INTO `grades` VALUES (64,'Final',78.00,100,'C'),(65,'Final',50.00,100,'A'),(66,'Endterm',25.00,50,'C'),(66,'Midterm',12.00,30,'C'),(66,'Project',12.00,20,'C'),(67,'Endsem',10.00,30,'D'),(67,'Midsem',10.00,30,'D'),(67,'Project',10.00,15,'D'),(67,'Quiz',10.00,25,'D'),(68,'Endsem',10.00,30,'C'),(68,'Midsem',10.00,30,'C'),(68,'Quiz',10.00,40,'C'),(69,'Endsem',10.00,25,'A'),(69,'Midsem',10.00,25,'A'),(69,'Project',50.00,50,'A'),(70,'Endterm',10.00,50,'C'),(70,'Midterm',10.00,30,'C'),(70,'Project',10.00,20,'C'),(71,'Endsem',10.00,30,'D'),(71,'Midsem',10.00,30,'D'),(71,'Project',10.00,15,'D'),(71,'Quiz',10.00,25,'D'),(72,'Endsem',30.00,30,'C'),(72,'Midsem',10.00,30,'C'),(72,'Quiz',10.00,40,'C'),(73,'Endsem',10.00,25,'A'),(73,'Midsem',10.00,25,'A'),(73,'Project',10.00,50,'A'),(74,'Final',50.00,100,'A'),(75,'Final',74.00,100,'C');
/*!40000 ALTER TABLE `grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instructors`
--

DROP TABLE IF EXISTS `instructors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instructors` (
  `instructor_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `department` varchar(50) DEFAULT NULL,
  `designation` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`instructor_id`),
  KEY `fk_instructor_user` (`user_id`),
  CONSTRAINT `fk_instructor_user` FOREIGN KEY (`user_id`) REFERENCES `erp_auth`.`users_auth` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instructors`
--

LOCK TABLES `instructors` WRITE;
/*!40000 ALTER TABLE `instructors` DISABLE KEYS */;
INSERT INTO `instructors` VALUES (27,41,'Electronics','Professor','Jane Doe'),(28,52,'Electronics','Professor','Sumit Darak'),(29,53,'Computer Science','Assistant Professor','Sumit Darak'),(30,57,'Computer Science','Professor','Sumbhdho'),(31,58,'Mathematics','Assistant Professor','Prahlab Deb'),(32,59,'Electronics','Visiting Faculty','Abhishek Kumar'),(33,62,'Electronics','Professor','ins'),(34,64,'Design','Professor','Gaurav sahu');
/*!40000 ALTER TABLE `instructors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sections`
--

DROP TABLE IF EXISTS `sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sections` (
  `section_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `instructor_id` int DEFAULT NULL,
  `day_time` varchar(50) DEFAULT NULL,
  `room` varchar(20) DEFAULT NULL,
  `capacity` int NOT NULL,
  `semester` varchar(10) DEFAULT NULL,
  `year` int DEFAULT NULL,
  `grading_scheme` json DEFAULT NULL,
  `current_enrollment` int DEFAULT '0',
  PRIMARY KEY (`section_id`),
  KEY `fk_section_course` (`course_id`),
  KEY `fk_section_instructor` (`instructor_id`),
  CONSTRAINT `fk_section_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_section_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `instructors` (`instructor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sections`
--

LOCK TABLES `sections` WRITE;
/*!40000 ALTER TABLE `sections` DISABLE KEYS */;
INSERT INTO `sections` VALUES (1,1,28,'Mon 11:00-12:00','B-103',60,'1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(2,2,32,'Tue 09:00-10:00','B-102',55,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(3,3,32,'Wed 09:00-10:00','B-103',70,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(4,4,NULL,'Thu 09:00-10:00','B-104',65,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(5,5,NULL,'Fri 09:00-10:00','B-105',75,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(6,6,NULL,'Mon 10:00-11:00','B-106',58,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(7,7,NULL,'Tue 10:00-11:00','B-107',64,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(8,8,NULL,'Wed 10:00-11:00','B-108',72,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(9,9,32,'Thu 10:00-11:00','B-109',63,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(10,10,NULL,'Fri 10:00-11:00','B-110',69,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(11,11,NULL,'Mon 11:00-12:00','B-111',68,'Sem3',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(12,12,31,'Tue 11:00-12:00','B-112',55,'Sem3',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(13,13,NULL,'Wed 11:00-12:00','B-113',73,'Sem3',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(14,14,NULL,'Thu 11:00-12:00','B-114',66,'Sem3',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(15,15,NULL,'Fri 11:00-12:00','B-115',61,'Sem3',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(16,16,32,'Mon 12:00-01:00','B-116',60,'Sem4',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(17,17,NULL,'Tue 12:00-01:00','B-117',58,'Sem4',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(18,18,NULL,'Wed 12:00-01:00','B-118',71,'Sem4',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(19,19,NULL,'Thu 12:00-01:00','B-119',64,'Sem4',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(20,20,NULL,'Fri 12:00-01:00','B-120',69,'Sem4',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(21,21,NULL,'Mon 01:00-02:00','B-121',76,'Sem5',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(22,22,NULL,'Tue 01:00-02:00','B-122',65,'Sem5',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(23,23,NULL,'Wed 01:00-02:00','B-123',72,'Sem5',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(24,24,NULL,'Thu 01:00-02:00','B-124',63,'Sem5',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(25,25,NULL,'Fri 01:00-02:00','B-125',59,'Sem5',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(26,26,NULL,'Mon 02:00-03:00','B-126',70,'Sem6',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(27,27,NULL,'Tue 02:00-03:00','B-127',67,'Sem6',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(28,28,NULL,'Wed 02:00-03:00','B-128',52,'Sem6',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(29,29,NULL,'Thu 02:00-03:00','B-129',79,'Sem6',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(30,30,NULL,'Fri 02:00-03:00','B-130',62,'Sem6',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(31,31,NULL,'Mon 03:00-04:00','B-131',68,'Sem7',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(32,32,NULL,'Tue 03:00-04:00','B-132',75,'Sem7',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(33,33,NULL,'Wed 03:00-04:00','B-133',58,'Sem7',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(34,34,NULL,'Thu 03:00-04:00','B-134',72,'Sem7',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(35,35,NULL,'Fri 03:00-04:00','B-135',61,'Sem7',2024,'{\"EndSem\": 40, \"Midterm\": 20, \"Quizzes\": 20, \"Assignments\": 20}',0),(36,36,NULL,'Mon 04:00-05:00','B-136',66,'Sem8',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(37,37,NULL,'Tue 04:00-05:00','B-137',73,'Sem8',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(38,38,NULL,'Wed 04:00-05:00','B-138',57,'Sem8',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(39,39,NULL,'Thu 04:00-05:00','B-139',60,'Sem8',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(40,40,NULL,'Fri 04:00-05:00','B-140',59,'Sem8',2024,'{\"EndSem\": 30, \"Midterm\": 20, \"Assignments\": 20, \"MiniProject\": 20, \"ClassParticipation\": 10}',0),(41,41,NULL,'Mon 08:00-09:00','B-141',62,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(42,42,NULL,'Tue 08:00-09:00','B-142',58,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(43,43,NULL,'Wed 08:00-09:00','B-143',69,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(44,44,NULL,'Thu 08:00-09:00','B-144',73,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(45,45,NULL,'Fri 08:00-09:00','B-145',61,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(46,21,NULL,'Mon 08:00-09:00','B-201',62,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(47,22,NULL,'Tue 08:00-09:00','B-202',58,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(48,23,NULL,'Wed 08:00-09:00','B-203',71,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(49,24,NULL,'Thu 08:00-09:00','B-204',66,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(50,25,NULL,'Fri 08:00-09:00','B-205',73,'Sem1',2024,'{\"EndSem\": 30, \"Midterm\": 25, \"Project\": 15, \"Quizzes\": 10, \"Assignments\": 20}',0),(51,26,NULL,'Mon 09:00-10:00','B-206',60,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(52,27,NULL,'Tue 09:00-10:00','B-207',64,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(53,28,NULL,'Wed 09:00-10:00','B-208',68,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(54,29,NULL,'Thu 09:00-10:00','B-209',72,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(55,30,NULL,'Fri 09:00-10:00','B-210',63,'Sem2',2024,'{\"Viva\": 10, \"EndSem\": 30, \"LabWork\": 40, \"Midterm\": 20}',0),(60,1,32,'Tue 11:00-02:00','C202',40,'4',2024,'{}',0),(61,1,28,'Wed 04:30 - 06:00','C211',200,'Sem3',2024,'{}',0),(62,82,28,'Mon 06:00 - 07:00','LR1',30,'Sem1',2024,'{}',0),(63,7,28,'Wed 11:00 - 01:00','C101',150,'Sem1',2024,'{}',0),(64,7,28,'Fri 02:00 - 04:00','C101',300,'Sem3',2024,'{}',0),(65,15,NULL,'Thu 11:00 - 12:00','D890',100,'Sem2',2024,'{}',0),(66,15,NULL,'Thu 11:00 - 12:00','X902',100,'Sem2',2024,'{}',0),(67,2,29,'Mon 08:00 - 09:00','k858',200,'Sem3',2024,'{}',0),(68,3,28,'Mon 12:00 - 01:00','A09',1,'Sem2',2024,'{}',0),(69,9,NULL,'Mon 11:00 - 12:00','l90',50,'Sem8',2024,'{}',0),(70,16,NULL,'Mon 06:00 - 07:00','C201',500,'Sem2',2024,'{}',0),(71,16,NULL,'Tue 03:00 - 05:00','C302',200,'Sem2',2024,'{}',0),(72,1,32,'Mon 12:00 - 01:00','x3',200,'Sem4',2024,'{}',0),(73,8,NULL,'Mon 01:00 - 03:00','C404',200,'Sem2',2024,'{}',0),(74,2,NULL,'Mon 11:00 - 12:00','A202',200,'Sem3',2024,'{}',0),(75,3,NULL,'Mon 12:00 - 02:00','S202',100,'Sem3',2024,'{}',0),(76,3,NULL,'Tue 03:00 - 04:00','202',200,'Sem5',2024,'{}',0),(77,86,28,'','C403',302,'2',2024,'{}',0),(78,1,33,'Thu 12:00 - 01:00','D505',202,'Sem1',2025,'{\"Endterm\": 50, \"Midterm\": 30, \"Project\": 20}',2),(79,2,33,'Fri 12:00 - 02:00','r909',300,'Sem1',2024,'{\"Quiz\": 25, \"Endsem\": 30, \"Midsem\": 30, \"Project\": 15}',2),(80,4,33,'Mon 03:00 - 05:00','C102',300,'Sem1',2025,'{\"Quiz\": 40, \"Endsem\": 30, \"Midsem\": 30}',2),(81,5,33,'Tue 04:00 - 06:00','L303',100,'Sem1',2024,'{\"Endsem\": 25, \"Midsem\": 25, \"Project\": 50}',2),(82,88,33,'Mon 12:00 - 01:00','ww',500,'Sem1',2024,'{\"Final\": 100}',2),(83,89,33,'Mon 02:00 - 06:00','S300',200,'Sem1',2024,'{\"Final\": 100}',2);
/*!40000 ALTER TABLE `sections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settings` (
  `setting_key` varchar(50) NOT NULL,
  `setting_value` varchar(100) NOT NULL,
  PRIMARY KEY (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settings`
--

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;
INSERT INTO `settings` VALUES ('maintenance_mode','true');
/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `roll_no` varchar(20) NOT NULL,
  `program` varchar(50) NOT NULL,
  `department` varchar(50) NOT NULL,
  `semester` int NOT NULL,
  `cgpa` decimal(3,2) NOT NULL DEFAULT '0.00',
  `year` year NOT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `roll_no` (`roll_no`),
  KEY `fk_student_user` (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `erp_auth`.`users_auth` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,37,'John Doe','101','Computer Science','Engineering',3,0.00,2024),(2,43,'Saksham Madan','2024493','B.Tech','CSSS',3,0.00,2025),(3,44,'ABCD','56789','B.Tech','CB',2,0.00,2045),(4,45,'DDDD','23330','M.Tech','CSB',4,0.00,2025),(5,46,'Rohan he ji','2024565','B.Tech','ECE',4,0.00,2028),(6,47,'A','20','M.Tech','ECE',1,0.00,2054),(7,48,'Mritunjay Poddar','2024358','B.Tech','CSSS',3,0.00,2025),(8,51,'tyytytytytyt','444444','M.Tech','CSB',5,0.00,2025),(9,55,'Satyam Vohra','2024258','B.Tech','CSSS',4,0.00,2025),(10,56,'Bondagram','2322','M.Tech','CSB',2,0.00,2025),(11,60,'student','398','M.Tech','CSE',1,0.00,2024),(12,61,'student1','2025','B.Tech','CSE',1,0.00,2025),(13,63,'Kumar2.0','2024357','B.Tech','CSB',3,0.00,2024);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-26 16:07:33

CREATE DATABASE IF NOT EXISTS erp_auth;
USE erp_auth;

-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: erp_auth
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
-- Table structure for table `users_auth`
--

DROP TABLE IF EXISTS `users_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_auth` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `role_` enum('admin','instructor','user') NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `status` varchar(20) DEFAULT 'active',
  `last_login` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `failed_attempts` int DEFAULT '0',
  `lockout_until` bigint DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_auth`
--

LOCK TABLES `users_auth` WRITE;
/*!40000 ALTER TABLE `users_auth` DISABLE KEYS */;
INSERT INTO `users_auth` VALUES (1,'Mohit','admin','admin123','active','2025-10-04 12:24:25',0,0),(2,'Mritunjay','admin','admin456','active','2025-10-04 12:26:54',0,0),(4,'stu1','user','student456','active','2025-10-04 12:30:08',0,0),(5,'Student2','user','$2a$12$RTdcu0ZtH6XHIa3.A0hehuNNgc./qJPSl/nkqFSo0Si0uDtqYHzg.','active','2025-11-04 16:30:32',0,0),(7,'Student3','user','$2a$12$HH0ZUyZSbiZixgT02Up9wej72IWtAW6WsHghBHuVTZcp1PeYQ6KOG','active','2025-11-04 16:31:35',0,0),(37,'john.doe','user','$2a$04$yP5xcL8RMcHD9dnqkv6vPuhlQybwwI98ug.T0z0aMO6mOL9/HfFhu','active','2025-11-17 08:58:58',0,0),(41,'jdefddoe','instructor','$2a$07$wE.w6dGNegd/p735S9t1R.Hf9Zhu8.96NXYalzAEJU3YUUbJ7eRWi','active','2025-11-17 09:38:08',0,0),(42,'admin','admin','$2a$13$LIkH5Zkakcn0hQ22rR4ohev1oCP2TEbDnaVD5gxnSJX7TIEMe1iDG','active','2025-11-17 09:43:17',0,0),(43,'Saksham.Madan','user','$2a$09$.EwIlc51UCn/qwnwc1bzYe4PfgwQOJE/mgcNLastwsTfWOeZSMxuq','active','2025-11-19 11:10:07',0,0),(44,'abcd','user','$2a$10$gP4zX7MiFwyKbP6eNiWopOFVPro8c26J7.pg37EQF9aoqVnZRsK5a','active','2025-11-19 11:24:34',0,0),(45,'ddd','user','$2a$13$wXyRFm/RD6QQ0gX0OpO0hubQbJMPEF7.dKBA1Jt4GoBVKoOvVszE6','active','2025-11-19 11:29:15',0,0),(46,'Rohan.Gagan','user','$2a$13$41QNp69oxL2CFPv0lGcgou1JHgDQuekvy1fvpcp1kKYmT8HhVD/KK','active','2025-11-19 11:34:52',0,0),(47,'A','user','$2a$10$/Yu7ce2YV3EueWUjfWD08.yt77.WA/hZwtiS0nE.HTsvJRmNVmDRO','active','2025-11-19 11:37:15',0,0),(48,'Mritunjay.Poddar','user','$2a$06$.biHdSdRr44IUBh631IFCuvUczAzXL3LnjOlOZYFEBExfqcPVm0dG','active','2025-11-19 11:38:55',0,0),(49,'fdfd','user','$2a$13$4E/kjUVxpBQYYRturN/UEu.TPmMMVxjiBDfi.gwGM5jNnEyV3o5WC','active','2025-11-19 11:44:35',0,0),(50,'E','user','$2a$13$GeGt82VhGlikVsBB/H7J7.eGG81Gcy3AfpqwPLz/YNrt3QYGMxEBK','active','2025-11-19 11:44:49',0,0),(51,'Absssss','user','$2a$07$O39OZJIYjVnQuo1.JSxqH.Qk8unu9BYK6u1wBaLyFkprAFYxAHuVK','active','2025-11-19 11:45:55',0,0),(52,'Sumit.Darak','instructor','$2a$04$bn16OiJOeg0vOVWjvccZM.mOZYUc6S/I4f6255o6htz4778NgmxCG','active','2025-11-19 12:38:16',0,0),(53,'Sumit-Darak','instructor','$2a$04$/.n.G0UbLqGg3j4aFE/qKOLMoJ0.q.CsfJCrIlVre2l7O7UfWtpwi','active','2025-11-19 12:39:21',0,0),(54,'Mark.Wood','admin','$2a$04$KF3E1hbv3fH8wnPyL.D3ae9cK8C0T6wZxY7H6Xo6iqiCaJCkh.LWS','active','2025-11-19 13:02:39',0,0),(55,'Satyam.Vohra','user','$2a$04$YrUPSUgSNFfZnGXR7W8mLu3Aa2QJ6hxWSmbypggP/SHgKVAUoC.z6','active','2025-11-19 16:43:59',0,0),(56,'Bonda','user','$2a$08$YXVS5IYDs5pv6EM1Dbbk/.KieA/crlzotUh..iIFhLOSc9efxkNY.','active','2025-11-19 17:05:11',0,0),(57,'Sumbhdho.','instructor','$2a$04$j4.bNy.AojcLFEyErhyNsOx4o5AcsLVwujNdQQvcxYZLO3cX2DH82','active','2025-11-19 17:06:46',0,0),(58,'Prahlad.deb','instructor','$2a$10$47oMC6ggzicr6//v3nu1kOkahY8IxREjol9MGCWbJrhKDN/b9w4GS','active','2025-11-19 20:20:15',0,0),(59,'Abhishek.Kumar','instructor','$2a$06$CrCTVC4kLcf66acaFBcvCeHQ/r4pZLq1roXYGQojv5smO8IYDOREm','active','2025-11-20 16:44:23',0,0),(60,'student','user','$2a$13$g7c8VJN.9v5rvFG7FxeO4eRbLdd5V6YnEPLX7J6uxZbMaEar06YyO','active','2025-11-22 17:16:40',0,0),(61,'student1','user','$2a$07$pvkq2U4lONahD/t.Xug1Fewj5QQsPnK1d80syDK6vRXDujJTHAonW','active','2025-11-23 06:43:53',0,0),(62,'ins','instructor','$2a$07$E/inwsXhcuk9A.LzMzNV3uAIZNwgNGtsf8Q6hhm/h0alOBvL.NTr2','active','2025-11-23 14:33:21',0,0),(63,'MK2.0','user','$2a$04$cU5/ZrWfJCJ.TtQekB3RrO.vdyqe.49nvcQpnmGUrsQcu00lfE4u6','active','2025-11-24 07:41:31',0,0),(64,'Gaurav.Sir','instructor','$2a$06$3j7a2ibe/lnv1k00FRFosu0Sjpa461UFGMsHL3j.VQR1Km.r0R/QC','active','2025-11-24 07:42:12',0,0);
/*!40000 ALTER TABLE `users_auth` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-26 16:06:24

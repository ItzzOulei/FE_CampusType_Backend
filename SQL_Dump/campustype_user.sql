-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: railway
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `bio` text,
  `Keyboard` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (132,'oulei','$2a$10$h8FIAtAMQwXH6gklmnaoxOD6pHsj4KeqILduMwmS8cbfvzGdwuGbG','oulei@oulei.oulei','Kumalala kumalala kumalala savesta Kumalala kumalala kumalala savesta Kumalala kumalala kumalala savesta Kumalala kumalala kumalala savesta Kumalala kumalala kumalala savesta ','Zuoya GMK87'),(135,'thataintme','$2a$10$ZcVT7EZ/xcKjsMt9A68r0uKemAVkrdxykiWCPSFpTRLp5MaUqnH6m','that@aint.me','Levy stinkt :)','undefined'),(136,'hebuhans','$2a$10$zdXQec2DfLvzpkY98JvoseD2PEhcYKVJSc1EJE14HLC.ga8nDiPim','hebuhans@hebu.hans',NULL,NULL),(137,'flaktario','$2a$10$1mmlqEoWH9c55mdPjrUMme4HdxznGyJHJ5aPFvpcrt9Fmj0I0FNgu','flaktario@gmail.com',NULL,NULL),(138,'hebuhans2','$2a$10$aAiptOPlsiHL92D/9kULA.8DGcWKM.xA8Djie.T/yp6/VsTS9gjAG','hebuhans2@hebu.hans',NULL,NULL),(139,'amogus','$2a$10$xfHkVyhU/CCACP5srZ7P2uhFAwsdkqty41o7Ijz.13pxaMH7O8/VC','amogus@amogus.amogus',NULL,NULL),(140,'andrin_racer','$2a$10$Kj1/dNrRCJTGntnkcuVRmuJo//txmhSqK.YVVPolJIWYw2R5Ee0I2','andrin.racer@bmw.de','Assalamaleykum','Basic ahh Keyboard'),(142,'cyrill','$2a$10$Xu0K2Yx27AEqOoaqr3h5dOsTjutxuUxFAp3R5AwRG/gI5EmB7oSwK','lil@gamdr.com',NULL,NULL),(143,'amogi','$2a$10$5OQtWwD1.cBq2nMxuvCEoublNQSPr9NWuiBwCWABOzI8bfDdINnXq','chicken@burger.ch',NULL,NULL),(144,'loic steiner','$2a$10$1jMs2KTbyNrkEV21DWELUe4obDyBf4I3efyTvj6wkooDLe7qg6N96','loic.steiner@ict-campus.net',NULL,NULL),(145,'joemama','$2a$10$qPh4I4ppTjN2unOf0ngmpurkEh2YH/AHmnMvwEbXerKk.Pi4gu5pu','joe@joe.joe',NULL,NULL),(160,'ouleii','$2a$10$hr8b1GMkubb.a5.TdQzDCOqCQ8BluHojztPJmAv67q/OlBmQlqdOe','oulei@oulei.test',NULL,NULL),(161,'lega','$2a$10$2qLEVnRQ1aom0OBPqelpCOBI5D4t3eJKzOKjOp3QEucYuCXmP76f6','lega@lega.ch','error: user.bio not found','GMK67'),(163,'rocker','$2a$10$VMlmX.vU88kZ0LL3IpwO1Omb4pLmPoD0GCmXPDzj/UdcuQl2G6JlK','asdfasdf@asdfasd.com',NULL,NULL),(165,'amogus32','$2a$10$FIAdxkBpd55n.UoZlfXW0OwIlhy1vs7cjxJD9LrWqBBrmbdxcC/ta','amogus@1234.ch',NULL,NULL),(167,'loic','$2a$10$/on5SZMOvbqWR7SqccChf.ydZTK7TutITk0I0JmIxo30uDNVSNw46','loic@loic.ch',NULL,NULL),(168,'asdf','$2a$10$IptB.NTMmkFU2tmVpGNvj.KPIXdolKcgbYoSdOhSlGWeyp.MfNfZG','asdf@asdf.asdf',NULL,NULL),(171,'yvonne','$2a$10$lmZOtyvGFj.CA//sYm4KhexkBgN5c08Wi0PbHv/ijATApvGM1Ize6','yvonne@test.test',NULL,NULL),(172,'goaty','$2a$10$6Sx8ZvhGH9YGaKh1UKGYpe94Vh3isBM8PmcziZ57rvmK03ICiykie','cr7@goat.pt',NULL,NULL),(173,'loicsteiner','$2a$10$Qw1rITWSzmK84ptFaKvteejTRysMer1zg2OjSt92OwDaTivTXTGim','loic@steiner.ch',NULL,NULL),(174,'vinh','$2a$10$LGDWpYBOptTsA5I2IiJoz.iNVEOAodktxZp9ZvIFFxnha3aQDNdfq','huu.le@ict-campus.net',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-10 18:44:21

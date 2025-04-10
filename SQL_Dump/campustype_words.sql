-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: campustype
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
-- Table structure for table `words`
--

DROP TABLE IF EXISTS `words`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `words` (
  `word` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `words`
--

LOCK TABLES `words` WRITE;
/*!40000 ALTER TABLE `words` DISABLE KEYS */;
INSERT INTO `words` VALUES ('the'),('be'),('to'),('of'),('and'),('a'),('in'),('that'),('have'),('i'),('it'),('for'),('not'),('on'),('with'),('he'),('as'),('you'),('do'),('at'),('this'),('but'),('his'),('by'),('from'),('they'),('we'),('say'),('her'),('she'),('or'),('an'),('will'),('my'),('one'),('all'),('would'),('there'),('their'),('what'),('so'),('up'),('out'),('if'),('about'),('who'),('get'),('which'),('go'),('me'),('when'),('make'),('can'),('like'),('time'),('no'),('just'),('him'),('know'),('take'),('people'),('into'),('year'),('your'),('good'),('some'),('could'),('them'),('see'),('other'),('than'),('then'),('now'),('look'),('only'),('come'),('its'),('over'),('think'),('also'),('back'),('after'),('use'),('two'),('how'),('our'),('work'),('first'),('well'),('way'),('even'),('new'),('want'),('because'),('any'),('these'),('give'),('day'),('most'),('us'),('is'),('are'),('was'),('were'),('had'),('has'),('been'),('am'),('did'),('does'),('said'),('got'),('made'),('went'),('saw'),('came'),('took'),('knew'),('thought'),('found'),('put'),('tell'),('where'),('more'),('too'),('should'),('need'),('right'),('here'),('off'),('before'),('same'),('many'),('much'),('such'),('long'),('why'),('thing'),('down'),('own'),('under'),('place'),('old'),('little'),('great'),('another'),('big'),('end'),('still'),('man'),('again'),('life'),('few'),('being'),('may'),('high'),('different'),('home'),('through'),('last'),('might'),('next'),('must'),('three'),('very'),('between'),('part'),('since'),('around'),('far'),('both'),('always'),('those'),('while'),('state'),('never'),('world'),('really'),('show'),('together'),('ask'),('without'),('turn'),('mean'),('call'),('find'),('hand'),('seem'),('against'),('keep'),('face'),('start'),('point'),('large'),('child'),('play'),('small'),('group'),('night'),('live'),('early'),('course'),('left'),('until'),('set'),('open'),('follow'),('change'),('kind'),('house');
/*!40000 ALTER TABLE `words` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-10 18:44:19

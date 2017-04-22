
--
-- Table structure for table `srt_files`
--
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `srt_files`;
CREATE TABLE `srt_files` (
  `file_name` varchar(255) NOT NULL,
  `language` varchar(255) NULL,
  PRIMARY KEY (`file_name`)
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=UTF32;

--
-- Table structure for table `subtitles`
--
DROP TABLE IF EXISTS `subtitles`;
CREATE TABLE `subtitles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start` timestamp NULL,
  `end` timestamp NULL,
  `srt_file` varchar(255) NULL,
  `content` varchar(255) NULL,
  `language` varchar(255) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_subtitles__srt_file` FOREIGN KEY (`srt_file`) REFERENCES `srt_files` (`file_name`)
);

--
-- Table structure for table `matches`
--
DROP TABLE IF EXISTS `matches`;
CREATE TABLE `matches` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start` timestamp NULL,
  `end` timestamp NULL,
  `ref_content` varchar(255) NULL,
  `target_content` varchar(255) NULL,
  `ref_language` varchar(4) NULL,
  `target_language` varchar(4) NULL,
  `hash_code` varchar(255) NULL,
  `ref_file` varchar(255) NULL,
  PRIMARY KEY (`id`)
);

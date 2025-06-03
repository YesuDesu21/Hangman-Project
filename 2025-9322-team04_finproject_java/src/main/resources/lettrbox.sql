-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: May 28, 2025 at 07:57 AM
-- Server version: 8.0.40
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lettrbox`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `adminid` varchar(3) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(7) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`adminid`, `username`, `password`) VALUES
('ad1', 'admin01', 'admin01passW'),
('ad2', 'admin02', 'admin02passW');

-- --------------------------------------------------------

--
-- Table structure for table `gameconfig`
--

CREATE TABLE `gameconfig` (
  `configid` varchar(9) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `waittime` int NOT NULL,
  `roundduration` int NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `gameconfig`
--

INSERT INTO `gameconfig` (`configid`, `waittime`, `roundduration`) VALUES
('config001', 10, 30);

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE `games` (
  `gameid` varchar(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `timecreated` datetime(6) NOT NULL,
  `status` varchar(9) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'Ongoing',
  `winner` varchar(50) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `games`
--

INSERT INTO `games` (`gameid`, `timecreated`, `status`, `winner`) VALUES
('game01', '2025-04-10 14:45:00.000000', 'completed', 'jasmine'),
('game02', '2025-05-25 13:29:51.348000', 'completed', 'alvin'),
('game03', '2025-05-28 15:24:59.843000', 'completed', 'johnrey'),
('game04', '2025-05-28 15:34:29.544000', 'completed', 'kernel'),
('game05', '2025-05-28 15:43:25.405000', 'completed', 'francis'),
('game06', '2025-05-28 15:48:20.122000', 'completed', 'alwin'),
('game07', '2025-05-28 15:50:54.845000', 'completed', 'jasper');

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE `players` (
  `playerid` varchar(10) DEFAULT NULL,
  `username` varchar(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `gameswon` int DEFAULT NULL,
  `loggedin` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`playerid`, `username`, `password`, `gameswon`, `loggedin`) VALUES
('player01', 'alvin', 'tolentino', 1, 0),
('player02', 'johnrey', 'geronimo', 1, 0),
('player03', 'alwin', 'garcia', 1, 0),
('player04', 'jasmine', 'espejo', 1, 0),
('player05', 'jasper', 'doria', 1, 0),
('player06', 'kernel', 'delapena', 1, 0),
('player07', 'francis', 'calulut', 1, 0);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

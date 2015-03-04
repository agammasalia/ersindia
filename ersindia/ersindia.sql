-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 21, 2014 at 05:04 PM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ersindia`
--
CREATE DATABASE IF NOT EXISTS `a1603000_ers` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `ersindia`;

-- --------------------------------------------------------

--
-- Table structure for table `postoffer`
--

CREATE TABLE IF NOT EXISTS `postoffer` (
  `pid` int(10) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `city` varchar(50) NOT NULL,
  `source` varchar(50) NOT NULL,
  `destination` varchar(50) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `gender` varchar(15) NOT NULL,
  `type` varchar(15) NOT NULL,
  `vehicle` varchar(10) NOT NULL,
  `vehiclenumber` varchar(15) NOT NULL,
  `vacancy` int(2) NOT NULL,
  `fare` int(9) NOT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB  DEFAULT CHARSET=ascii AUTO_INCREMENT=53 ;

--
-- Table structure for table `requests`
--

CREATE TABLE IF NOT EXISTS `requests` (
  `pid` int(100) NOT NULL,
  `postemail` varchar(100) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `requestemail` varchar(100) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `pickup` varchar(50) NOT NULL,
  `drop` varchar(50) NOT NULL,
  `accepted` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=ascii;

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `unique_id` varchar(23) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `encrypted_password` varchar(80) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `salt` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `emailaddress` varchar(80) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `number` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `gender` varchar(6) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `dob` date NOT NULL,
  `address` varchar(300) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `rating` int(10) NOT NULL,
  `gcm_regID` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique_id` (`unique_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=ascii AUTO_INCREMENT=33 ;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

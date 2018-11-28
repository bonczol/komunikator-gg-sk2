#pragma once
#include "pch.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <conio.h>
#include <map>
#include <list>
#include <boost/archive/text_oarchive.hpp>
#include <boost/archive/text_iarchive.hpp>

class Klient {
private:
	friend class boost::serialization::access;
	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & nick;
		ar & login;  
		ar & password;
		ar & friends;
		ar & ID_convs;
	}
public:
	//login, klient
	static std::map<std::string, Klient*> CLIENTS;
	static pthread_mutex_t clients_mutex = PTHREAD_MUTEX_INITIALIZER;

	std::string nick;
	std::string login;
	std::string password;
	bool logged_in;
	int socket;
	std::list<Klient*> friends;
	//lista id konwersacji
	std::list<int> ID_convs;

	Klient(std::string nick, std::string login, std::string password,
		bool logged_in = false, int socket = -1);
	static void initialize_clients();
};
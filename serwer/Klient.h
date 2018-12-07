#pragma once
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <map>
#include <list>
#include <string>
#include <boost/archive/text_oarchive.hpp>
#include <boost/archive/text_iarchive.hpp>

class Klient {
public:
	//login, klient
	static std::map<std::string, Klient*> CLIENTS;
	static pthread_mutex_t clients_mutex;
	std::string nick;
	std::string login;
	std::string password;
	bool logged_in;
	std::string description;
	int socket;
	std::list<Klient*> friends;
	//lista id konwersacji
	std::list<int> ID_convs;

	Klient(std::string nick, std::string login, std::string password);
	Klient(){}

	static void initialize_clients();

	friend class boost::serialization::access;
	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & nick;
		ar & login;  
		ar & password;
		ar & description;
		ar & friends;
		ar & ID_convs;
	}

	std::string str_log(){
		if(this->logged_in) return "1";
		return "0";
	}

	friend std::ostream& operator<< (std::ostream& os, const Klient& klient) {
		os << klient.nick << " : " << klient.login << " : " <<
		 klient.password  << " : " << klient.description << " : " << klient.logged_in;
		return os;
	}
};
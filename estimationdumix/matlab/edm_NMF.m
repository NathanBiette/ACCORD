function [coefs] = edm_NMF(n, delta, beta, epsilon, save_result, print, nb_essai)
nbinstru = 4;

% programme qui fait la NMF 

% n : longueur de l'échantillon pour la TF
% delta : pas entre chaque échantillon pour faire le diagramme TF
% beta : c'est le beta de la divergence-beta
% epsilon : marge d'erreur pour la méthode d'optimisation
% save_result : si égal à 1, les données sont sauvegardées dans un fichier dans le dossier results
% sinon rien
% print : si égal à 1, le résultat est affiché dans un graphique
% nb_essai : chaîne de caractère représentant la donnée à utiliser '01' pour essai01, '02' pour essai02...

if save_result == 1
  load('../results/compteurs.mat');
end;

[xbatterie, xmelodie, xbasse, xaccompagnement, xglobal, N] = load_audio(nb_essai, n, delta, 1);

% matrice contenant les TF de chaque instrument dans chaque colonne
% Cette matrice depend du temps (on avance colonne par colonne dans les
% differents diagrammes temps-fr�quance)
W = zeros([n nbinstru]);
compteur = 0;

leng = floor((N-n)/delta);


coefs = zeros([4 leng]);
compteurs = zeros([1 leng]);

tic;
for j=1:leng
    % on met la TF du i-eme instrument a l'instant j dans
    % la i-eme colonne de W
    W(:,1) = xmelodie(:,j);
    W(:,2) = xaccompagnement(:,j);
    W(:,3) = xbasse(:,j);
    W(:,4) = xbatterie(:,j);

    % c sera le nombre d'instruments effectivement
    % en train de jouer
    c = 4;

    % on enregistre dans liste, les indices des
    % instruments qui ne jouent pas
    % pour que notre probleme ait une solution
    % (car il faut que les blocs de W soient inversibles)
    liste = [];
    for k=1:nbinstru
        if W(:,k) == zeros([n 1])
            c = c - 1;
        else
            liste = horzcat(liste, [k]);
        end;
    end;

    % On construit Wprim a partir de ces
    % instruments qui jouent (il y en a c)
    Wprim = zeros([n c]);
    
    for k=1:c
        Wprim(:,k) = W(:,liste(k));
    end;

    V = xglobal(:,j);
    H = 2*ones([c 1]);

    [Xprim, compteurs(j)] = methode_nonNul(V, Wprim, H, beta, epsilon);

    % Xprim ne contient que les instruments qui jouent
    % (et pas ceux qu'on a enleve un peu plus haut)
    % il faut donc reconstruire X en mettant des zeros
    % pour les instruments qui ne jouent pas
    decalage = 0;
    X = zeros([4 1]);
    for k=1:nbinstru

        % si k est dans liste on retrouve sa valeur dans Xprim
        if any(k==liste)
            X(k) = Xprim(k-decalage);

        % sinon, on met 0
        else
            X(k) = 0;
            decalage = decalage + 1;
        end;
    end;
    
    % on place X dans le diagramme temporel des coefficients
    coefs(:,j) = X;

end;
temps_traitement = toc;

algo = fileread('multiplication_resolution.m');

% on construit l'echelle des abscisses
absc = zeros([1 leng]);

for j=1:length(coefs)
    absc(j) = j;
end;


if save_result==1
  main_code = fileread('multiplication_resolution.m');
  info = strcat('../Data/essai',nb_essai,'/info');
  filename = strcat('../results/NMF_essai',nb_essai,'_',num2str(compteur_nmf, '%03.0f'),'.mat');
  save(filename, 'main_code', 'info', 'n', 'delta', 'coefs', 'temps_traitement', 'compteurs', 'beta', 'epsilon');
  compteur_nmf = compteur_nmf + 1;
  save('../results/compteurs', 'compteur_pseudoinv2', 'compteur_pseudoinv1', 'compteur_nmf');
end;

if print == 1
  figure;
  plot(absc,coefs(1,:), absc, coefs(2,:), absc, coefs(3,:), absc, coefs(4,:));
end;
return;
end
